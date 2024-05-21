package br.com.alterdata.vendas.controller;

import br.com.alterdata.vendas.model.Categoria;
import br.com.alterdata.vendas.service.CategoriaService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso",
            content = @Content(schema = @Schema(implementation = Categoria.class)))
    @GetMapping
    public ResponseEntity<?> listar() {
        return categoriaService.listar();
    }

    @PostMapping("/cadastrar")
    @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso",
            content = @Content(schema = @Schema(implementation = Categoria.class)))
    public ResponseEntity<?> cadastrar(@RequestBody Categoria categoria) {
        return categoriaService.cadastrar(categoria);
    }

    @ApiResponse(responseCode = "200", description = "Categoria editada com sucesso",
            content = @Content(schema = @Schema(implementation = Categoria.class)))
    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody Categoria categoria) {
        return categoriaService.editar(categoria);
    }

    @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable long id) {
        return categoriaService.deletar(id);
    }

}
