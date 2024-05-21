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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@Slf4j
@SpringBootTest(
        classes = {VendasApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integracao")
public class ListarTodosProdutosComCategorias {

    @Autowired private WebApplicationContext webAppContextSetup;
    @Autowired private EntityManager em;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria listar todos os produtos com categorias")
    public void deveriaListarTodosProdutosComCategoria() {
        when().get("/produtos")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("id", hasItems(1, 2, 3, 4))
                .body("nome", hasItems(
                        "Motosserra Semi Profissional Gasolina 50,2Cc Tcs53X-20",
                        "Tesoura Para Poda + Serrote de Poda",
                        "Kit para Jardinagem Horta Tramontina 04 Peças com Luva",
                        "Enxada"))
                .body("descricao", hasItems(
                        "As Motosserras Toyama Foram Desenvolvidas E Fabricadas Especialmente Para O Usuário Que Busca Qualidade De Equipamento Com Baixo Custo De Aquisição. Para Torná-La Leve, Durável E Confiável Utilizamos Em Sua Produção, Liga De Magnésio De Alta Resistência, Equipadas Com As Correntes Oregon.",
                        "Tesourão para Poda Linha Bronze, para podar frutíferas, flores e plantas ornamentais, galhos e ramos de árvores onde não é possível alcançar com as tesouras normais. Para ajudar no seu trabalho, esse jogo contém um serrote para poda com 2 lâminas.",
                        "Kit para Jardinagem Horta Tramontina 04 Peças com Luva. Kit completo com a qualidade Tramontina para pequenas hortas, jardins, hobby, floricultura.",
                        "Enxada Tramontina para uso em jardins ou reformas."))
                .body("referencia", hasItems("TCS53H18", "KIT140", "55374237", "ENX1234"))
                .body("valorUnitario", hasItems(399.90f, 163.54f, 57.34f, 80.50f))
                .body("categoria.id", hasItems(1, 2, 3))
                .body("categoria.nome", hasItems("Eletronico", "Jardim", "Kit"));
    }


    /*

     @MockBean
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    @Sql("/seeds/produtosCategoriasUsuarios.sql")
    @Test
    @DisplayName("Deveria retornar erro ao listar produtos quando ocorre uma exceção")
    public void deveriaRetornarErroAoListarProdutosQuandoOcorreUmaExcecao() {
        Mockito.doThrow(new RuntimeException("Erro simulado")).when(produtoRepository).findAll();

        when().get("/produtos")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(equalTo("Erro ao listar produtos Erro simulado"));
    }
    */

}
