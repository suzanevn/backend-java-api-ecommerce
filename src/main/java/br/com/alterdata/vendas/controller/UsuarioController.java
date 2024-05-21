package br.com.alterdata.vendas.controller;

import br.com.alterdata.vendas.model.Produto;
import br.com.alterdata.vendas.model.Usuario;
import br.com.alterdata.vendas.service.UsuarioService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @ApiResponse(responseCode = "200", description = "Usuarios listados com sucesso",
            content = @Content(schema = @Schema(implementation = Usuario.class)))
    @GetMapping
    public ResponseEntity<?> listar() {
        return usuarioService.listar();
    }

    @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = Usuario.class)))
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.cadastrar(usuario);
    }

    @ApiResponse(responseCode = "200", description = "Usuario editado com sucesso",
            content = @Content(schema = @Schema(implementation = Usuario.class)))
    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody Usuario usuario) {
        return usuarioService.editar(usuario);
    }

    @ApiResponse(responseCode = "200", description = "Usuario deletado com sucesso")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return usuarioService.deletar(id);
    }

    @ApiResponse(responseCode = "200", description = "Usuario filtrado por login",
            content = @Content(schema = @Schema(implementation = Usuario.class)))
    @GetMapping("/{login}")
    public ResponseEntity<?> buscarUsuarioPorLogin(@PathVariable String login) {
        return usuarioService.buscarUsuarioPorLogin(login);
    }

}

