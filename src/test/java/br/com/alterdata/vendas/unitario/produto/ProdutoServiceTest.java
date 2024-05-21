package br.com.alterdata.vendas.unitario.produto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.repository.CategoriaRepository;
import br.com.alterdata.vendas.repository.ProdutoRepository;
import br.com.alterdata.vendas.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock ProdutoRepository produtoRepository;

    @Mock private CategoriaRepository categoriaRepository;
    @InjectMocks private ProdutoService service;

    Categoria categoria;
    Produto produto;
    @BeforeEach
    public void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Jardim");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Pá tamanho grande");
        produto.setDescricao("Pá para jardinagem");
        produto.setReferencia("XYZ123");
        produto.setValorUnitario(new BigDecimal("50.00"));
        produto.setCategoria(categoria);
    }
    @Test
    @DisplayName("deveria retornar a listagem de produtos")
    public void deveriaRetornarListagemProdutos() {
        service.listar();
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveria retornar a listagem de produtos com categoria")
    public void deveriaRetornarListagemProdutosComCategoria() {
        Categoria categoria1 = new Categoria(1L, "Categoria 1");
        Categoria categoria2 = new Categoria(2L, "Categoria 2");
        Produto produto1 = new Produto(1L, "Produto 1", "Descrição 1", "REF1", BigDecimal.valueOf(10.0), categoria1);
        Produto produto2 = new Produto(2L, "Produto 2", "Descrição 2", "REF2", BigDecimal.valueOf(20.0), categoria2);
        List<Produto> produtos = Arrays.asList(produto1, produto2);

        when(produtoRepository.findAll()).thenReturn(produtos);

        ResponseEntity<?> responseEntity = service.listar();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(produtos, responseEntity.getBody());
        List<Produto> resultado = (List<Produto>) responseEntity.getBody();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Produto 1", resultado.get(0).getNome());
        assertNotNull(resultado.get(0).getCategoria());
        assertEquals("Categoria 1", resultado.get(0).getCategoria().getNome());
        assertEquals("Produto 2", resultado.get(1).getNome());
        assertNotNull(resultado.get(1).getCategoria());
        assertEquals("Categoria 2", resultado.get(1).getCategoria().getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deveria retornar erro ao listar produtos")
    public void deveriaRetornarErroAoListarProdutos() {
        when(produtoRepository.findAll()).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = service.listar();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao listar produtos ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria cadastrar produto com sucesso")
    public void deveriaCadastrarProdutoComSucesso() {
        Produto produto = new Produto();
        produto.setNome("Pá tamanho grande");
        produto.setDescricao("Pá para jardinagem");
        produto.setReferencia("XYZ123");
        produto.setValorUnitario(new BigDecimal("50.00"));
        produto.setCategoria(categoria);

        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any())).thenReturn(produto);

        ResponseEntity<?> responseEntity = service.cadastrar(produto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(produto, responseEntity.getBody());
        Produto produtoSalvo = (Produto) responseEntity.getBody();
        assertNotNull(produtoSalvo);
        assertEquals("Pá tamanho grande", produtoSalvo.getNome());
        assertEquals("Pá para jardinagem", produtoSalvo.getDescricao());
        assertEquals("XYZ123", produtoSalvo.getReferencia());
        assertEquals(new BigDecimal("50.00"), produtoSalvo.getValorUnitario());
        assertEquals(categoria, produtoSalvo.getCategoria());
    }

    @Test
    @DisplayName("deveria lançar exceção ao cadastrar produto")
    public void deveriaLancarExcecaoAoCadastrarProdutoComNomeNull() {
        Produto produto = new Produto();
        produto.setNome(null);
        produto.setDescricao("Descrição do Produto Teste");
        produto.setReferencia("REF123");
        produto.setValorUnitario(new BigDecimal("100.00"));

        when(produtoRepository.save(produto)).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = service.cadastrar(produto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao salvar o produto ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao cadastrar produto com categoria inválida")
    public void deveriaLancarExcecaoAoCadastrarProdutoComCategoriaInvalida() {
        Categoria c = new Categoria();
        c.setId(2L);
        c.setNome("invalida");
        produto.setCategoria(c);
        when(categoriaRepository.findById(c.getId())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = service.cadastrar(produto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Categoria não encontrada", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria editar produto com sucesso")
    public void deveriaEditarProdutoComSucesso() {
        produto.setNome("Nome alterado");
        produto.setDescricao("Descrição alterada");
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNome("Categoria");
        produto.setCategoria(categoria2);

        when(produtoRepository.saveAndFlush(any())).thenReturn(produto);
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria2));

        ResponseEntity<?> responseEntity = service.editar(produto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(produto, responseEntity.getBody());
        Produto produtoEditado = (Produto) responseEntity.getBody();
        assertNotNull(produtoEditado);
        assertEquals("Nome alterado", produtoEditado.getNome());
        assertEquals("Descrição alterada", produtoEditado.getDescricao());
        assertEquals("XYZ123", produtoEditado.getReferencia());
        assertEquals(new BigDecimal("50.00"), produtoEditado.getValorUnitario());
        assertEquals(categoria2, produtoEditado.getCategoria());
    }

    @Test
    @DisplayName("deveria retornar erro ao editar produto")
    public void deveriaRetornarErroAoEditarProduto() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(produtoRepository.saveAndFlush(any())).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = service.editar(produto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao editar produto ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao editar produto com id inexistente")
    public void deveriaLancarExcecaoAoEditarProdutoComIdInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = service.editar(produto);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Produto com esse id não foi encontrado", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria deletar produto com sucesso")
    public void deveriaDeletarProdutoComSucesso() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(produto));

        ResponseEntity<?> responseEntity = service.deletar(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Produto deletado com sucesso", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao deletar produto com id inexistente")
    public void deveriaRetornarErroAoDeletarProdutoComIdInexistente() {
        when(produtoRepository.findById(6L)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = service.deletar(6L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Produto com esse id não foi encontrado", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao deletar produto")
    public void deveriaLancarExcecaoAoDeletarProduto() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.of(produto));
        doThrow(new RuntimeException("")).when(produtoRepository).deleteById(anyLong());

        ResponseEntity<?> responseEntity = service.deletar(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao deletar o produto ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria buscar produto por categoria")
    public void deveriaBuscarProdutosPorCategoria() {
        Categoria categoria = new Categoria(1L, "Categoria Teste");
        Produto produto1 = new Produto(1L, "Produto 1", "Descrição 1", "REF1", BigDecimal.valueOf(10.0), categoria);
        Produto produto2 = new Produto(2L, "Produto 2", "Descrição 2", "REF2", BigDecimal.valueOf(20.0), categoria);
        List<Produto> produtos = Arrays.asList(produto1, produto2);

        when(produtoRepository.findByCategoriaId(categoria.getId())).thenReturn(produtos);

        ResponseEntity<?> responseEntity = service.buscarPorCategoria(categoria.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(produtos, responseEntity.getBody());
        List<Produto> produtosRetornados = (List<Produto>) responseEntity.getBody();
        assertNotNull(produtosRetornados);
        assertEquals(2, produtosRetornados.size());
        assertTrue(produtosRetornados.contains(produto1));
        assertTrue(produtosRetornados.contains(produto2));
        verify(produtoRepository, times(1)).findByCategoriaId(categoria.getId());
    }

    @Test
    @DisplayName("deveria retornar erro ao buscar produto por categoria inexistente")
    public void deveriaLancarErroAoBuscarProdutoPorCategoriaInexistente() {
        when(produtoRepository.findByCategoriaId(50L)).thenReturn(null);

        ResponseEntity<?> responseEntity = service.buscarPorCategoria(50L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Nenhum Produto encontrado com a Categoria informada", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao buscar produto por categoria")
    public void deveriaLancarErroAoBuscarProdutoPorCategoria() {
        when(produtoRepository.findByCategoriaId(anyLong())).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = service.buscarPorCategoria(50L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao buscar produtos por categoria ", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria pesquisar produtos pela descrição de qualquer campo do produto")
    public void deveriaPesquisarProdutosPelaDescricaoDeQualquerCampoDoProduto() {
        String descricaoPesquisa = "Descrição";
        Produto produto1 = new Produto(1L, "Produto 1", "Descrição 1", "REF1", BigDecimal.valueOf(10.0), new Categoria());
        Produto produto2 = new Produto(2L, "Produto 2", "Descrição 2", "REF2", BigDecimal.valueOf(20.0), new Categoria());
        List<Produto> produtos = Arrays.asList(produto1, produto2);

        when(produtoRepository.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa)).thenReturn(produtos);

        ResponseEntity<?> responseEntity = service.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(produtos, responseEntity.getBody());
        List<Produto> produtosRetornados = (List<Produto>) responseEntity.getBody();
        assertNotNull(produtosRetornados);
        assertEquals(2, produtosRetornados.size());
        assertTrue(produtosRetornados.contains(produto1));
        assertTrue(produtosRetornados.contains(produto2));
        verify(produtoRepository, times(1)).pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa);
    }

    @Test
    @DisplayName("deveria retornar erro ao pesquisar produtos pela descrição de qualquer campo do produto")
    public void deveriaLancarErroAoPesquisarProdutosPelaDescricaoDeQualquerCampoDoProduto() {
        String descricaoPesquisa = "Descrição";

        when(produtoRepository.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa)).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> responseEntity = service.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao pesquisar produtos: Erro", responseEntity.getBody());
    }

}
