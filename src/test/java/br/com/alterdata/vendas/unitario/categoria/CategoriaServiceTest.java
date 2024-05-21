package br.com.alterdata.vendas.unitario.categoria;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.repository.CategoriaRepository;
import br.com.alterdata.vendas.repository.ProdutoRepository;
import br.com.alterdata.vendas.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Ferramentas");
    }

    @Test
    @DisplayName("deveria listar categorias com sucesso")
    public void deveriaListarCategoriasComSucesso() {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(categoria);

        when(categoriaRepository.findAll()).thenReturn(categorias);

        ResponseEntity<?> responseEntity = categoriaService.listar();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(categorias, responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao listar categorias")
    public void deveriaRetornarErroAoListarCategorias() {
        when(categoriaRepository.findAll()).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = categoriaService.listar();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao listar categorias ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria cadastrar categoria com sucesso")
    public void deveriaCadastrarCategoriaComSucesso() {
        when(categoriaRepository.save(any())).thenReturn(categoria);

        ResponseEntity<?> responseEntity = categoriaService.cadastrar(categoria);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(categoria, responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao cadastrar categoria")
    public void deveriaRetornarErroAoCadastrarCategoria() {
        when(categoriaRepository.save(any())).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = categoriaService.cadastrar(categoria);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao salvar categoria ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria editar categoria com sucesso")
    public void deveriaEditarCategoriaComSucesso() {
        when(categoriaRepository.saveAndFlush(any())).thenReturn(categoria);
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        categoria.setNome("Outra categoria");

        ResponseEntity<?> responseEntity = categoriaService.editar(categoria);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(categoria, responseEntity.getBody());
        Categoria categoriaRetornada = (Categoria) responseEntity.getBody();
        assertNotNull(categoriaRetornada);
        assertEquals(categoria.getNome(), categoriaRetornada.getNome());
    }

    @Test
    @DisplayName("deveria retornar erro ao editar categoria")
    public void deveriaRetornarErroAoEditarCategoria() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(categoriaRepository.saveAndFlush(any())).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = categoriaService.editar(categoria);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao editar categoria ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao editar categoria com Id inexistente")
    public void deveriaRetornarErroAoEditarCategoriaComIdInexistente() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = categoriaService.editar(categoria);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Categoria com esse id não foi encontrada", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria deletar categoria com sucesso")
    public void deveriaDeletarCategoriaComSucesso() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).deleteById(anyLong());

        ResponseEntity<?> responseEntity = categoriaService.deletar(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Categoria deletada com sucesso", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria lancar excecao ao deletar categoria")
    public void deveriaLancarExcecaoAoDeletarCategoria() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        doThrow(new RuntimeException("")).when(categoriaRepository).deleteById(anyLong());

        ResponseEntity<?> responseEntity = categoriaService.deletar(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao deletar categoria ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao deletar Categoria sendo utilizada por Produto")
    public void deveriaRetornarErroAoDeletarCategoriaSendoUtilizadaPorProduto() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto());
        when(produtoRepository.findByCategoriaId(anyLong())).thenReturn(produtos);
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));

        ResponseEntity<?> responseEntity = categoriaService.deletar(1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Categoria está sendo utilizada por produtos", responseEntity.getBody());
        verify(categoriaRepository, never()).deleteById(anyLong());
    }

}