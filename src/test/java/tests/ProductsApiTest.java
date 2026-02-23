package tests;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payloads.ProductsAddPayloads;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class ProductsApiTest extends BaseTest {

    //Testes para GET /products
    @Test
    @DisplayName("Deve retornar produtos com sucesso")
    public void deveRetornarProdutosComSucesso() {
        given()
        .when()
            .get(ApiPaths.PRODUCTS)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("products", not(empty()))
            .body("total", greaterThan(0))
            .body("limit", greaterThan(0));
    }

    //Testes para GET /products/{id}
    @Test
    @DisplayName("Deve retornar produto por ID")
    public void deveRetornarProdutoPorId() {
        int productId = 1;

        given()
        .when()
            .get(ApiPaths.PRODUCTS_ID, productId)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(productId))
            .body("title", notNullValue())
            .body("price", notNullValue())
            .body("brand", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar erro de produto não encontrado")
    public void deveRetornarErroProdutoNaoEncontrado() {
        int productId = 0;

        given()
        .when()
            .get(ApiPaths.PRODUCTS_ID, productId)
        .then()
            .log().ifValidationFails()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", is("Product with id '" + productId + "' not found"));
    }

    @Test
    @DisplayName("Deve retornar erro de produto inválido")
    public void deveRetornarErroProdutoInvalido() {
        String productId = "essence";

        given()
        .when()
            .get(ApiPaths.PRODUCTS_ID, productId)
        .then()
            .log().ifValidationFails()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", is("Product with id '" + productId + "' not found"));
    }

    @Test
    @DisplayName("Deve retornar erro de método inválido - GET /products")
    public void deveRetornarErroMetodoInvalido_getProducts() {
        int productId = 1;

        given()
            .contentType(ContentType.JSON)
        .when()
            .post(ApiPaths.PRODUCTS_ID, productId)
        .then()
            .log().ifValidationFails()
            .statusCode(anyOf(is(404), is(405)))
            .body(containsString("Cannot POST /products/"+productId+""));
    }

    //Testes para GET /products/search
    @Test
    @DisplayName("Deve retornar produto por parâmetro")
    public void deveRetornarProdutoPorParametro() {
        String title = "Red Lipstick";

        List<Map<String, Object>> products =
            given()
                .queryParam("q", title)
            .when()
                .get(ApiPaths.PRODUCTS_SEARCH)
            .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("products", hasSize(greaterThan(0)))
                .body("total", greaterThan(0))
                .body("products.title", notNullValue())
                .extract().jsonPath().get("products");

        for (Map<String, Object> product : products) {
            String titleRetornado = (String) product.get("title");
            Assertions.assertEquals(title, titleRetornado, "O title retornado '" + titleRetornado + "' não corresponde ao title esperado '" + title + "'");
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar parâmetro inexistente - GET /products")
    public void deveRetornarVazioBuscaParametroInexistente_getProducts() {
        String title = "Black Lipstick";

        given()
            .queryParam("q", title)
        .when()
            .get(ApiPaths.PRODUCTS_SEARCH)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("products", empty())
            .body("total", is(0));
    }

    @Test
    @DisplayName("Deve ignorar parâmetros diferentes de Q na busca de produto")
    public void deveIgnorarParametrosDiferentesDeQNaBuscaDeProduto() {
        String chave = "brand";
        Object valor = "Calvin Klein";

        given()
            .queryParam(chave, valor)
        .when()
            .get(ApiPaths.PRODUCTS_SEARCH)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("products", not(empty()))
            .body("total", greaterThan(0))
            .body("limit", greaterThan(0))
            .body("products.title", everyItem(notNullValue()));
    }

    //Testes para POST /products/add
    @Test
    @DisplayName("Deve criar produto com sucesso")
    public void deveCriarProdutoComSucesso() {
        given()
            .header("Content-Type", "application/json")
            .body(ProductsAddPayloads.produtoValido())
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body("title", is("Perfume Oil"));
    }

    @Test
    @DisplayName("Deve criar produto sem parâmetros")
    public void deveCriarProdutoSemParametros() {
        given()
            .header("Content-Type", "application/json")
            .body("{}")
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0));
    }

    @Test
    @DisplayName("Deve criar produto sem parâmetros importantes")
    public void deveCriarProdutoSemParametrosImportantes() {
        given()
            .header("Content-Type", "application/json")
            .body(ProductsAddPayloads.produtoSemParametrosEspecificos("title", "description", "price"))
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body("$", not(hasKey("title")))
            .body("$", not(hasKey("description")))
            .body("$", not(hasKey("price")));
    }

    @Test
    @DisplayName("Deve ignorar o id informado e criar produto")
    public void deveIgnorarIdInformadoECriarProduto() {
        String chave = "id";
        var valor = 1;

        given()
            .header("Content-Type", "application/json")
            .body(ProductsAddPayloads.produtoComParametroAdicional(chave, valor))
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body("id", not(valor));
    }

    @Test
    @DisplayName("Deve ignorar parâmetros extras e criar produto")
    public void deveIgnorarParametrosExtrasECriarProduto() {
        String chave = "parameter";
        var valor = 123;

        given()
            .header("Content-Type", "application/json")
            .body(ProductsAddPayloads.produtoComParametroAdicional(chave, valor))
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body("$", not(hasKey(chave)));
    }

    @Test
    @DisplayName("Deve aceitar valor inválido para chaves")
    public void deveAceitarValorInvalidoParaChaves() {
        String chave = "price";
        Object valor = "Number";

        given()
            .header("Content-Type", "application/json")
            .body(ProductsAddPayloads.produtoComParametroAdicional(chave, valor))
        .when()
            .post(ApiPaths.PRODUCTS_ADD)
        .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body(chave, is(valor));
    }
}
