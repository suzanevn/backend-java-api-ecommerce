package br.com.alterdata.vendas.integracao.produto;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class EditarProdutos {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria editar um produto")
    public void deveriaEditarProduto() {
        String novoProdutoJson = """
            {
                "id": 4,
                "nome": "Produto Editado",
                "descricao": "Descrição do Produto Editado",
                "referencia": "REF00EDIT",
                "valorUnitario": 50.00,
                "categoria": {
                    "id": 2
                }
            }
        """;

        given()
                .contentType("application/json")
                .body(novoProdutoJson)
                .when()
                .put("/produtos/editar")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(notNullValue()))
                .body("nome", is("Produto Editado"))
                .body("descricao", is("Descrição do Produto Editado"))
                .body("referencia", is("REF00EDIT"))
                .body("valorUnitario", is(50.00f))
                .body("categoria.id", is(2));
    }

}
