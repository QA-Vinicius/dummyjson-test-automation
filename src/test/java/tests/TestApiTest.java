package tests;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestApiTest extends BaseTest {
    @Test
    @DisplayName("Validar status da aplicação")
    public void validarStatusAplicacao() {
        given().
            when()
                .log().ifValidationFails()
                .get(ApiPaths.TEST)
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("ok"))
                .body("method", equalTo("GET"))
                .log().ifValidationFails();
    }
}
