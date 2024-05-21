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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class DeletarCategorias {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria deletar uma categoria existente")
    public void deveriaDeletarCategoriaExistente() throws Exception {
        String idCategoria = "4";

        given()
                .when()
                .delete("/categorias/deletar/{id}", idCategoria)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /*
    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria retornar erro ao deletar categoria usada por produtos")
    public void deveriaRetornarErroAoDeletarCategoriaUsadaPorProdutos() throws Exception {
        String idCategoria = "1";

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/categorias/deletar/{id}", idCategoria)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Categoria está sendo utilizada por produtos"));
    }
    */

}
