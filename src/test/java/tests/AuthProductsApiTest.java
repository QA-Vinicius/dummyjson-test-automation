package tests;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.AuthService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThan;

public class AuthProductsApiTest extends BaseTest {
    @Test
    @DisplayName("Deve retornar lista de produtos após login com sucesso")
    public void deveRetornarListaProdutosAposLoginSucesso() {
        String accessToken = AuthService.gerarAccessToken("emilys", "emilyspass");

        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
        .when()
            .get(ApiPaths.AUTH_PRODUCTS)
        .then()
            .log().ifValidationFails()
            .contentType(ContentType.JSON)
            .statusCode(200)
            .body("products", not(empty()))
            .body("total", greaterThan(0))
            .body("limit", greaterThan(0));
    }

    @Test
    @DisplayName("Deve retornar erro de token inválido/expirado")
    public void deveRetornarErroTokenInvalidoOuExpirado() {
        String accessToken = "token";

        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
        .when()
            .get(ApiPaths.AUTH_PRODUCTS)
        .then()
            .log().ifValidationFails()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid/Expired Token!"));
    }

    @Test
    @DisplayName("Deve retornar erro ao não informar o token")
    public void deveRetornarErroAoNaoInformarToken() {
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "")
        .when()
            .get(ApiPaths.AUTH_PRODUCTS)
        .then()
            .log().ifValidationFails()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Access Token is required"));
    }
}
