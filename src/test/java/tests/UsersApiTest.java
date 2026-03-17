package tests;

import config.BaseTest;
import endpoints.ApiPaths;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class UsersApiTest extends BaseTest {
    @Test
    @DisplayName("Deve retornar usuários com sucesso")
    public void deveRetornarUsuariosComSucesso() {
        given()
        .when()
            .get(ApiPaths.USERS)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("users", not(empty()))
            .body("total", greaterThan(0))
            .body("limit", greaterThan(0))
            .body("users.username", everyItem(notNullValue()))
            .body("users.password", everyItem(notNullValue()));
    }

    @Test
    @DisplayName("Deve retornar usuário por ID")
    public void deveRetornarUsuarioPorId() {
        int userId = 1;

        given()
        .when()
            .get(ApiPaths.USERS_ID, userId)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(userId))
            .body("username", not(empty()))
            .body("password", not(empty()));
    }

    @Test
    @DisplayName("Deve retornar erro de usuário não encontrado")
    public void deveRetornarErroUsuarioNaoEncontrado() {
        int userId = 50000;

        given()
        .when()
            .get(ApiPaths.USERS_ID, userId)
        .then()
            .log().ifValidationFails()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", is("User with id '" + userId + "' not found"));
    }

    @Test
    @DisplayName("Deve retornar erro de usuário inválido")
    public void deveRetornarErroUsuarioInvalido() {
        String userId = "emilia";

        given()
        .when()
            .get(ApiPaths.USERS_ID, userId)
        .then()
            .log().ifValidationFails()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", is("Invalid user id '" + userId + "'"));
    }

    @Test
    @DisplayName("Deve retornar erro de método inválido")
    public void deveRetornarErroMetodoInvalido() {
        int userId = 1;

        given()
            .contentType(ContentType.JSON)
        .when()
            .post(ApiPaths.USERS_ID, userId)
        .then()
            .log().ifValidationFails()
            .statusCode(anyOf(is(404), is(405)))
            .body(containsString("Cannot POST /users/"+userId+""));
    }

    @Test
    @DisplayName("Deve retornar usuário por parâmetro")
    public void deveRetornarUsuarioPorParametro() {
        String username = "emilys";

        List<Map<String, Object>> users =
                given()
                    .queryParam("q", username)
                .when()
                    .get(ApiPaths.USERS_SEARCH)
                .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("users", hasSize(greaterThan(0)))
                    .body("total", greaterThan(0))
                    .body("username", not(empty()))
                    .body("password", not(empty()))
                    .extract().jsonPath().getList("users");

        for (Map<String, Object> user : users) {
            String usernameRetornado = (String) user.get("username");
            Assertions.assertEquals(username, usernameRetornado, "O username retornado '" + usernameRetornado + "' não corresponde ao username esperado '" + username + "'");
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar parâmetro inexistente")
    public void deveRetornarVazioBuscaParametroInexistente() {
        String username = "Vinicius";

        given()
            .queryParam("q", username)
        .when()
            .get(ApiPaths.USERS_SEARCH)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("users", empty())
            .body("total", is(0));
    }

    @Test
    @DisplayName("Deve ignorar parâmetros diferentes de Q na busca de usuário")
    public void deveIgnorarParametrosDiferentesDeQNaBuscaDeUsuario() {
        String chave = "age";
        var valor = 35;

        given()
            .queryParam(chave, valor)
        .when()
            .get(ApiPaths.USERS_SEARCH)
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("users", not(empty()))
            .body("total", greaterThan(0))
            .body("limit", greaterThan(0))
            .body("users.username", everyItem(notNullValue()))
            .body("users.password", everyItem(notNullValue()));
    }
}
