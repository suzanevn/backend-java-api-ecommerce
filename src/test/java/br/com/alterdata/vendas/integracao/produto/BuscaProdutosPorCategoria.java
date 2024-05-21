package br.com.alterdata.vendas.integracao.produto;

import br.com.alterdata.vendas.VendasApplication;
import br.com.alterdata.vendas.repository.ProdutoRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
public class BuscaProdutosPorCategoria {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @Mock
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Test
    @DisplayName("Deveria buscar produtos pela categoria 3")
    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    public void deveriaBuscarProdutosPorCategoria3() {
        given()
                .contentType("application/json")
                .pathParam("categoriaId", 3)
                .when()
                .get("/produtos/categoria/{categoriaId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2))
                .body("[0].id", is(2))
                .body("[0].nome", is("Tesoura Para Poda + Serrote de Poda"))
                .body("[0].descricao", is("Tesourão para Poda Linha Bronze, para podar frutíferas, flores e plantas ornamentais, galhos e ramos de árvores onde não é possível alcançar com as tesouras normais. Para ajudar no seu trabalho, esse jogo contém um serrote para poda com 2 lâminas."))
                .body("[0].referencia", is("KIT140"))
                .body("[0].valorUnitario", is(163.54f))
                .body("[0].categoria.id", is(3))
                .body("[1].id", is(3))
                .body("[1].nome", is("Kit para Jardinagem Horta Tramontina 04 Peças com Luva"))
                .body("[1].descricao", is("Kit para Jardinagem Horta Tramontina 04 Peças com Luva. Kit completo com a qualidade Tramontina para pequenas hortas, jardins, hobby, floricultura."))
                .body("[1].referencia", is("55374237"))
                .body("[1].valorUnitario", is(57.34f))
                .body("[1].categoria.id", is(3));
    }

    /*@Test
    @DisplayName("Deveria retornar erro ao buscar produtos por categoria")
    public void deveriaRetornarErroAoBuscarProdutosPorCategoria() {
        given()
                .contentType("application/json")
                .pathParam("categoriaId", 5)
                .when()
                .get("/produtos/categoria/{categoriaId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(equalTo("Nenhum Produto encontrado com a Categoria informada"));
    }*/

}
