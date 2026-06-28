package br.com.fiap.oficina.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClienteApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirCliente() {
        var clienteId =
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "nome", "Maria Silva",
                                "cpfCnpj", "12345678901",
                                "email", "maria@email.com",
                                "telefone", "11999999999"))
                .when()
                        .post("/clientes")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("nome", equalTo("Maria Silva"))
                        .body("cpfCnpj", equalTo("12345678901"))
                        .extract()
                        .jsonPath()
                        .getString("id");

        given()
                        .pathParam("id", clienteId)
                .when()
                        .get("/clientes/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(clienteId))
                        .body("nome", equalTo("Maria Silva"));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "nome", "Maria Atualizada",
                                "email", "maria.atualizada@email.com",
                                "telefone", "11888888888"))
                        .pathParam("id", clienteId)
                .when()
                        .put("/clientes/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(clienteId))
                        .body("nome", equalTo("Maria Atualizada"))
                        .body("email", equalTo("maria.atualizada@email.com"));

        given()
                .when()
                        .get("/clientes")
                .then()
                        .statusCode(200)
                        .body("$", hasSize(1))
                        .body("[0].id", equalTo(clienteId));

        given()
                        .pathParam("id", clienteId)
                .when()
                        .delete("/clientes/{id}")
                .then()
                        .statusCode(204);

        given()
                        .pathParam("id", clienteId)
                .when()
                        .get("/clientes/{id}")
                .then()
                        .statusCode(500)
                        .body("message", equalTo("Cliente nao encontrado."));
    }
}