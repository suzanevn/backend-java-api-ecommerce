package br.com.alterdata.vendas.controller;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.service.ProdutoService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("produtos")
public class ProdutosController {

    @Autowired private ProdutoService produtoService;

    @ApiResponse(responseCode = "200", description = "Produtos listados com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class)))
    @GetMapping
    public ResponseEntity<?> listar() {
        return produtoService.listar();
    }

    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class)))
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Produto p) {
        return produtoService.cadastrar(p);
    }

    @ApiResponse(responseCode = "200", description = "Produto editado com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class)))
    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody Produto p) {
        return produtoService.editar(p);
    }

    @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable long id) {
        return produtoService.deletar(id);
    }

    @ApiResponse(responseCode = "200", description = "Produtos filtrado por categoria",
            content = @Content(schema = @Schema(implementation = Produto.class)))
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable Long categoriaId) {
        return produtoService.buscarPorCategoria(categoriaId);
    }

    @ApiResponse(responseCode = "200", description = "Produtos filtrado por qualquer campo",
            content = @Content(schema = @Schema(implementation = Produto.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pesquisa-qualquer-campo")
    public ResponseEntity<?> pesquisaPelaDescricaoDeQualquerCampoDoProduto(@RequestParam("descricaoPesquisa") String descricaoPesquisa) {
        return produtoService.pesquisaPelaDescricaoDeQualquerCampoDoProduto(descricaoPesquisa);
    }

}
