package br.com.alterdata.vendas.service;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.repository.CategoriaRepository;
import java.util.List;

import br.com.alterdata.vendas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;


    public ResponseEntity<?> listar() {
        try {
            List<Categoria> categorias = categoriaRepository.findAll();
            return new ResponseEntity<>(categorias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao listar categorias " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> cadastrar(Categoria categoria) {
        try {
            return new ResponseEntity<>(categoriaRepository.save(categoria), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao salvar categoria " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> editar(Categoria categoria) {
        try {
            ResponseEntity<String> categoriaNaoExiste = verificaExistenciaCategoria(categoria.getId());
            if (categoriaNaoExiste != null) return categoriaNaoExiste;
            return new ResponseEntity<>(categoriaRepository.saveAndFlush(categoria), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao editar categoria " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deletar(Long id) {
        try {
            ResponseEntity<String> categoriaNaoExiste = verificaExistenciaCategoria(id);
            if (categoriaNaoExiste != null) return categoriaNaoExiste;
            List<Produto> produtos = produtoRepository.findByCategoriaId(id);
            if (!produtos.isEmpty()) {
                return new ResponseEntity<>("Categoria está sendo utilizada por produtos", HttpStatus.BAD_REQUEST);
            }
            categoriaRepository.deleteById(id);
            return new ResponseEntity<>("Categoria deletada com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar categoria " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> verificaExistenciaCategoria(Long id) {
        if (id == null || categoriaRepository.findById(id).orElse(null) == null) {
            return new ResponseEntity<>("Categoria com esse id não foi encontrada", HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
