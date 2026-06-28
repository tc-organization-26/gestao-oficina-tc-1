package br.com.fiap.oficina.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class VeiculoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirVeiculo() {
        var clienteId = criarCliente();

        var veiculoId =
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "clienteId", clienteId,
                                "placa", "ABC1D23",
                                "marca", "Toyota",
                                "modelo", "Corolla",
                                "ano", 2020))
                .when()
                        .post("/veiculos")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("clienteId", equalTo(clienteId))
                        .body("placa", equalTo("ABC1D23"))
                        .extract()
                        .jsonPath()
                        .getString("id");

        given()
                        .pathParam("id", veiculoId)
                .when()
                        .get("/veiculos/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(veiculoId))
                        .body("marca", equalTo("Toyota"));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "modelo", "Civic",
                                "marca", "Honda",
                                "ano", 2021))
                        .pathParam("id", veiculoId)
                .when()
                        .put("/veiculos/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(veiculoId))
                        .body("marca", equalTo("Honda"))
                        .body("modelo", equalTo("Civic"));

        given()
                .when()
                        .get("/veiculos")
                .then()
                        .statusCode(200)
                        .body("$", hasSize(1))
                        .body("[0].id", equalTo(veiculoId));

        given()
                        .pathParam("id", veiculoId)
                .when()
                        .delete("/veiculos/{id}")
                .then()
                        .statusCode(204);

        given()
                        .pathParam("id", veiculoId)
                .when()
                        .get("/veiculos/{id}")
                .then()
                        .statusCode(500)
                        .body("message", equalTo("Veiculo nao encontrado."));
    }

    private String criarCliente() {
        return given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "nome", "Joao Silva",
                                "cpfCnpj", "12345678901",
                                "email", "joao@email.com",
                                "telefone", "11999999999"))
                .when()
                        .post("/clientes")
                .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getString("id");
    }
}