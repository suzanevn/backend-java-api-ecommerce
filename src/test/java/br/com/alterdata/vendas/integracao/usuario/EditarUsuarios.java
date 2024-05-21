package br.com.alterdata.vendas.integracao.usuario;

import br.com.alterdata.vendas.VendasApplication;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class EditarUsuarios {
    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria editar um usuário existente")
    public void deveriaEditarUsuarioExistente() {
        String usuarioJson = """
            {
                "id": 1,
                "login": "adminEditado",
                "senha": "password",
                "role": "ADMIN"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(usuarioJson)
                .when()
                .put("/usuarios/editar")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("login", equalTo("adminEditado"))
                .body("role", equalTo("ADMIN"));
    }
}
