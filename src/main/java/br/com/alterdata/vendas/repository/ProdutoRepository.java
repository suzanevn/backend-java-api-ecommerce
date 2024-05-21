package br.com.alterdata.vendas.repository;

import br.com.alterdata.vendas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoriaId(Long categoriaId);

    @Query("SELECT p FROM Produto p JOIN p.categoria c WHERE " +
            "UPPER(p.nome) LIKE UPPER(CONCAT('%', :descricaoPesquisa, '%')) OR " +
            "UPPER(p.descricao) LIKE UPPER(CONCAT('%', :descricaoPesquisa, '%')) OR " +
            "UPPER(p.referencia) LIKE UPPER(CONCAT('%', :descricaoPesquisa, '%')) OR " +
            "UPPER(c.nome) LIKE UPPER(CONCAT('%', :descricaoPesquisa, '%')) OR " +
            "CAST(p.valorUnitario AS string) LIKE CONCAT('%', :descricaoPesquisa, '%')")
    List<Produto> pesquisaPelaDescricaoDeQualquerCampoDoProduto(@Param("descricaoPesquisa") String descricaoPesquisa);

}
