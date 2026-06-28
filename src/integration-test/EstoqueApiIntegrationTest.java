package br.com.fiap.oficina.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EstoqueApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarIncluirBaixarEExcluirItemEstoque() {
        var itemId =
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "codigo", "OLEO-5W30",
                                "descricao", "Oleo 5W30",
                                "valorUnitario", 45.90,
                                "quantidadeInicial", 10.000))
                .when()
                        .post("/estoque")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("codigo", equalTo("OLEO-5W30"))
                        .body("ativo", equalTo(true))
                        .extract()
                        .jsonPath()
                        .getString("id");

        given()
                        .pathParam("id", itemId)
                .when()
                        .get("/estoque/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(itemId))
                        .body("descricao", equalTo("Oleo 5W30"));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "descricao", "Oleo 5W30 sintetico",
                                "valorUnitario", 55.90))
                        .pathParam("id", itemId)
                .when()
                        .put("/estoque/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(itemId))
                        .body("descricao", equalTo("Oleo 5W30 sintetico"));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("quantidade", 5.000))
                        .pathParam("id", itemId)
                .when()
                        .post("/estoque/{id}/inclusoes")
                .then()
                        .statusCode(200)
                        .body("quantidadeDisponivel", equalTo(15.0f));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("quantidade", 3.000))
                        .pathParam("id", itemId)
                .when()
                        .post("/estoque/{id}/baixas")
                .then()
                        .statusCode(200)
                        .body("quantidadeDisponivel", equalTo(12.0f));

        given()
                .when()
                        .get("/estoque")
                .then()
                        .statusCode(200)
                        .body("$", hasSize(1))
                        .body("[0].id", equalTo(itemId));

        given()
                        .pathParam("id", itemId)
                .when()
                        .delete("/estoque/{id}")
                .then()
                        .statusCode(204);

        given()
                        .pathParam("id", itemId)
                .when()
                        .get("/estoque/{id}")
                .then()
                        .statusCode(200)
                        .body("ativo", equalTo(false));
    }
}