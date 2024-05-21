package br.com.alterdata.vendas.integracao.categoria;

import br.com.alterdata.vendas.VendasApplication;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class CadastrarCategorias {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria cadastrar uma nova categoria")
    public void deveriaCadastrarNovaCategoria() {
        String novoCategoriaJson = """
            {
                "nome": "Nova Categoria"
            }
        """;

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(novoCategoriaJson)
                .when()
                .post("/categorias/cadastrar")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(notNullValue()))
                .body("nome", equalTo("Nova Categoria"));
    }

}
