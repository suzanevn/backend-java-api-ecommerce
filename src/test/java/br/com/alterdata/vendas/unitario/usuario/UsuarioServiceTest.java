package br.com.alterdata.vendas.unitario.usuario;

import br.com.alterdata.vendas.model.Usuario;
import br.com.alterdata.vendas.repository.UsuarioRepository;
import br.com.alterdata.vendas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    Usuario usuario;
    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("teste");
        usuario.setSenha("senha");
        usuario.setRole("USER");
    }

    @Test
    @DisplayName("deveria listar usuarios com sucesso")
    public void deveriaListarUsuariosComSucesso() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuario);

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        ResponseEntity<?> responseEntity = usuarioService.listar();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(usuarios, responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria retornar erro ao listar usuarios")
    public void deveriaRetornarErroAoListarUsuarios() {
        when(usuarioRepository.findAll()).thenThrow(new RuntimeException(""));

        ResponseEntity<?> responseEntity = usuarioService.listar();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao listar usuarios ", responseEntity.getBody());
    }

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    public void deveCadastrarUsuarioComSucesso() {
        when(usuarioRepository.findByLogin("teste")).thenReturn(null);
        when(passwordEncoder.encode("senha")).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        ResponseEntity<?> responseEntity = usuarioService.cadastrar(usuario);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Usuario responseBody = (Usuario) responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getId());
        assertEquals("teste", responseBody.getLogin());
        assertEquals("senhaCodificada", responseBody.getSenha());
        assertEquals("USER", responseBody.getRole());
    }

    @Test
    @DisplayName("Deve retornar erro ao cadastrar usuário com login já existente")
    public void deveRetornarErroAoCadastrarUsuarioComLoginExistente() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario.setLogin("teste");

        when(usuarioRepository.findByLogin("teste")).thenReturn(usuario2);

        ResponseEntity<?> response = usuarioService.cadastrar(usuario);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Login já cadastrado", response.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao cadastrar usuario com login null")
    public void deveriaLancarExcecaoAoCadastrarUsuarioComLoginNull() {
        Usuario usuario = new Usuario();
        usuario.setLogin(null);
        usuario.setSenha("senha");
        usuario.setRole("USER");

        when(usuarioRepository.save(usuario)).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> responseEntity = usuarioService.cadastrar(usuario);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao cadastrar usuário Erro", responseEntity.getBody());
    }

    @Test
    @DisplayName("Deve editar um usuário com sucesso")
    public void deveEditarUsuarioComSucesso() {
        Usuario usuarioAlterado = new Usuario();
        usuarioAlterado.setId(1L);
        usuarioAlterado.setLogin("teste alterado");
        usuarioAlterado.setSenha("novaSenha");
        usuarioAlterado.setRole("USER");

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByLogin("teste")).thenReturn(usuario);
        when(passwordEncoder.encode("senha")).thenReturn("novaSenhaCodificada");
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuarioAlterado);

        ResponseEntity<?> response = usuarioService.editar(usuario);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioAlterado, response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro ao editar usuário com ID Inexistente")
    public void deveRetornarErroAoEditarUsuarioComIDInexistente() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = usuarioService.editar(usuario);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Usuário com esse id não foi encontrado", responseEntity.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao editar usuario com login null")
    public void deveriaLancarExcecaoAoEditarUsuarioComLoginNull() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByLogin("teste")).thenReturn(usuario);
        when(passwordEncoder.encode("senha")).thenReturn("novaSenhaCodificada");

        when(usuarioRepository.saveAndFlush(usuario)).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> responseEntity = usuarioService.editar(usuario);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao editar usuário Erro", responseEntity.getBody());
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    public void deveDeletarUsuarioComSucesso() {
        doNothing().when(usuarioRepository).deleteById(1L);

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioService.deletar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário deletado com sucesso", response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro ao deletar usuário inexistente")
    public void deveRetornarErroAoDeletarUsuarioInexistente() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioService.deletar(5L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuário com esse id não foi encontrado", response.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao deletar usuario")
    public void deveriaLancarExcecaoAoDeletarUsuario() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        doThrow(new RuntimeException("")).when(usuarioRepository).deleteById(anyLong());

        ResponseEntity<?> responseEntity = usuarioService.deletar(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao deletar usuário ", responseEntity.getBody());
    }

    @Test
    @DisplayName("Deve buscar um usuário por login com sucesso")
    public void deveBuscarUsuarioPorLoginComSucesso() {
        when(usuarioRepository.findByLogin("teste")).thenReturn(usuario);

        ResponseEntity<?> response = usuarioService.buscarUsuarioPorLogin("teste");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar usuário por login inexistente")
    public void deveRetornarErroAoBuscarUsuarioPorLoginInexistente() {
        when(usuarioRepository.findByLogin("testeInexistente")).thenReturn(null);

        ResponseEntity<?> response = usuarioService.buscarUsuarioPorLogin("testeInexistente");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuário não encontrado", response.getBody());
    }

    @Test
    @DisplayName("deveria lançar exceção ao buscar usuário por login")
    public void deveriaLancarExcecaoAoBuscarUsuarioPorLogin() {
        when(usuarioRepository.findByLogin("teste")).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> responseEntity = usuarioService.buscarUsuarioPorLogin("teste");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Erro ao pesquisar usuario por login Erro", responseEntity.getBody());
    }

}
