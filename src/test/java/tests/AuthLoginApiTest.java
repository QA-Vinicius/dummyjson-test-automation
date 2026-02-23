package tests;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payloads.AuthPayloads;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthLoginApiTest extends BaseTest {
    @Test
    @DisplayName("Deve realizar login com sucesso")
    public void deveRealizarLoginComSucesso() {
        String username = "emilys";
        String password = "emilyspass";

        given()
            .header("Content-Type", "application/json")
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("id", greaterThan(0))
            .body("username", equalTo(username))
            .body("email", notNullValue())
            .body("firstName", notNullValue())
            .body("lastName", notNullValue())
            .body("gender", notNullValue())
            .body("image", notNullValue())
            .body("accessToken", notNullValue())
            .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Deve retornar erro de credenciais inválidas")
    public void deveRetornarErroCredenciaisInvalidas() {
        String username = "emilys";
        String password = "emilyspass123";

        given()
            .header("Content-Type", "application/json")
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid credentials"));
    }

    @Test
    @DisplayName("Deve retornar erro de username inválido")
    public void deveRetornarErroFormatoUsernameInvalido() {
        Object username = 12345;
        String password = "emilyspass";

        given()
            .header("Content-Type", "application/json")
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Username is not valid"));
    }

    @Test
    @DisplayName("Deve retornar erro por falta de campo obrigatório sem username")
    public void deveRetornarErroCampoObrigatorioSemUsername() {
        String username = "";
        String password = "emilyspass";

        given()
            .header("Content-Type", "application/json")
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Username and password required"));
    }

    @Test
    @DisplayName("Deve retornar erro por falta de campo obrigatório sem password")
    public void deveRetornarErroCampoObrigatorioSemPassword() {
        String username = "emilys";
        String password = "";

        given()
            .header("Content-Type", "application/json")
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Username and password required"));
    }

    @Test
    @DisplayName("Deve retornar erro por falta de campos obrigatórios com payload vazio")
    public void deveRetornarErroCamposObrigatoriosPayloadVazio() {
        given()
            .header("Content-Type", "application/json")
            .body("{}")
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Username and password required"));
    }
}
