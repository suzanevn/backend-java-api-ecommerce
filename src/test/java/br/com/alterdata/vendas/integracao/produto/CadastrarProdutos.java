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
import static org.hamcrest.CoreMatchers.*;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class CadastrarProdutos {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }


    @Test
    @DisplayName("Deveria cadastrar um novo produto")
    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    public void deveriaCadastrarNovoProduto() {
        String novoProdutoJson = """
            {
                "nome": "Produto D",
                "descricao": "Descrição do Produto D",
                "referencia": "REF004",
                "valorUnitario": 40.00,
                "categoria": {
                    "id": 1
                }
            }
        """;

        given()
                .contentType("application/json")
                .body(novoProdutoJson)
                .when()
                .post("/produtos/cadastrar")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(notNullValue()))
                .body("nome", is("Produto D"))
                .body("descricao", is("Descrição do Produto D"))
                .body("referencia", is("REF004"))
                .body("valorUnitario", is(40.00f))
                .body("categoria.id", is(1));
    }

    /*@Test
    @DisplayName("Deveria retornar erro ao cadastrar produtos com categoria inexistente")
    public void deveriaRetornarErroAoCadastrarProdutosComCategoriaInexistente() {
        String novoProdutoJson = """
            {
                "nome": "Produto D",
                "descricao": "Descrição do Produto D",
                "referencia": "REF004",
                "valorUnitario": 40.00,
                "categoria": {
                    "id": 50
                }
            }
        """;
        given()
                .contentType("application/json")
                .body(novoProdutoJson)
                .when()
                .post("/produtos/cadastrar")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(equalTo("Nenhum Produto encontrado com a Categoria informada"));
    }*/

}
