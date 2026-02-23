package services;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import payloads.AuthPayloads;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// Classe responsavel por chamar a API POST /auth/login e extrair o statusCode para se utilizar no GET /auth/products
public class AuthService extends BaseTest {
    public static String gerarAccessToken(String username, String password) {
        return given()
            .contentType(ContentType.JSON)
            .body(AuthPayloads.loginPayload(username, password))
        .when()
            .post(ApiPaths.AUTH_LOGIN)
        .then()
            .statusCode(200)
            .body("accessToken", notNullValue())
            .extract()
            .path("accessToken");
    }
}
