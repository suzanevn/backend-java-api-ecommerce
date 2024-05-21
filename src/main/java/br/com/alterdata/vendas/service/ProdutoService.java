package br.com.alterdata.vendas.service;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.repository.CategoriaRepository;
import br.com.alterdata.vendas.repository.ProdutoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public ResponseEntity<?> listar() {
        try {
            return new ResponseEntity<>(produtoRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao listar produtos " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> cadastrar(Produto produto) {
        ResponseEntity<String> categoriaNaoExiste = verificaExistenciaCategoria(produto, true);
        if (categoriaNaoExiste != null) return categoriaNaoExiste;
        try {
            Produto produtoSalvo = produtoRepository.save(produto);
            return new ResponseEntity<>(produtoSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao salvar o produto " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> editar(Produto produto) {
        try {
            ResponseEntity<String> produtoNaoExiste = verificaExistenciaProduto(produto.getId());
            if (produtoNaoExiste != null) return produtoNaoExiste;
            ResponseEntity<String> categoriaNaoExiste = verificaExistenciaCategoria(produto, false);
            if (categoriaNaoExiste != null) return categoriaNaoExiste;
            return new ResponseEntity<>(produtoRepository.saveAndFlush(produto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao editar produto " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deletar(Long id) {
        try {
            ResponseEntity<String> produtoNaoExiste = verificaExistenciaProduto(id);
            if (produtoNaoExiste != null) return produtoNaoExiste;
            produtoRepository.deleteById(id);
            return new ResponseEntity<>("Produto deletado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar o produto " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> buscarPorCategoria(Long categoriaId) {
        try {
            List<Produto> produtosEncontrados = produtoRepository.findByCategoriaId(categoriaId);
            if (produtosEncontrados == null || produtosEncontrados.size() == 0){
                return new ResponseEntity<>("Nenhum Produto encontrado com a Categoria informada", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(produtosEncontrados, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao buscar produtos por categoria " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> pesquisaPelaDescricaoDeQualquerCampoDoProduto(String descricaoPesquisa) {
        try {
            List<Produto> produtos = produtoRepository.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa);
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao pesquisar produtos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> verificaExistenciaCategoria(Produto p, boolean cadastrar) {
        if (p.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(p.getCategoria().getId()).orElse(null);
            if (categoria == null) {
                return new ResponseEntity<>("Categoria não encontrada", HttpStatus.BAD_REQUEST);
            }
            if(cadastrar)
                p.setCategoria(categoria);
        }
        return null;
    }

    private ResponseEntity<String> verificaExistenciaProduto(Long id) {
        if (id ==null || produtoRepository.findById(id).orElse(null) == null) {
            return new ResponseEntity<>("Produto com esse id não foi encontrado", HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
