package br.com.alterdata.vendas.service;

import br.com.alterdata.vendas.model.Usuario;
import br.com.alterdata.vendas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> listar() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao listar usuarios " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> cadastrar(Usuario usuario) {
        try {
            ResponseEntity<String> loginExistente = validaLoginExistente(usuario);
            if (loginExistente != null) return loginExistente;
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao cadastrar usuário " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> editar(Usuario usuario) {
        try {
            ResponseEntity<String> usuarioNaoExiste = verificaExistenciaUsuario(usuario.getId());
            if (usuarioNaoExiste != null) return usuarioNaoExiste;
            ResponseEntity<String> loginExistente = validaLoginExistente(usuario);
            if (loginExistente != null) return loginExistente;
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            usuario = usuarioRepository.saveAndFlush(usuario);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao editar usuário " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deletar(Long id) {
        try {
            ResponseEntity<String> usuarioNaoExiste = verificaExistenciaUsuario(id);
            if (usuarioNaoExiste != null) return usuarioNaoExiste;
            usuarioRepository.deleteById(id);
            return new ResponseEntity<>("Usuário deletado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar usuário " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> buscarUsuarioPorLogin(String login) {
        try {
            Usuario usuario = usuarioRepository.findByLogin(login);
            if (usuario == null) {
                return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao pesquisar usuario por login " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> validaLoginExistente(Usuario usuario) {
        Usuario loginJaCadastrado = usuarioRepository.findByLogin(usuario.getLogin());
        if(loginJaCadastrado != null && loginJaCadastrado.getId()!=usuario.getId()){
            return new ResponseEntity<>("Login já cadastrado", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return null;
    }

    private ResponseEntity<String> verificaExistenciaUsuario(Long id) {
        if (id == null || usuarioRepository.findById(id).orElse(null) == null) {
            return new ResponseEntity<>("Usuário com esse id não foi encontrado", HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
