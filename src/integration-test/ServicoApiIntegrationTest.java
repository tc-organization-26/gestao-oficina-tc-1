package br.com.fiap.oficina.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirServico() {
        var servicoId =
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "codigo", "TROCA-OLEO",
                                "descricao", "Troca de oleo",
                                "valorUnitario", 120.50,
                                "tempoEstimadoMinutos", 60))
                .when()
                        .post("/servicos")
                .then()
                        .statusCode(201)
                        .body("id", notNullValue())
                        .body("codigo", equalTo("TROCA-OLEO"))
                        .extract()
                        .jsonPath()
                        .getString("id");

        given()
                        .pathParam("id", servicoId)
                .when()
                        .get("/servicos/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(servicoId))
                        .body("descricao", equalTo("Troca de oleo"));

        given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "descricao", "Troca de oleo premium",
                                "valorUnitario", 180.00,
                                "tempoEstimadoMinutos", 90))
                        .pathParam("id", servicoId)
                .when()
                        .put("/servicos/{id}")
                .then()
                        .statusCode(200)
                        .body("id", equalTo(servicoId))
                        .body("descricao", equalTo("Troca de oleo premium"))
                        .body("tempoEstimadoMinutos", equalTo(90));

        given()
                .when()
                        .get("/servicos")
                .then()
                        .statusCode(200)
                        .body("$", hasSize(1))
                        .body("[0].id", equalTo(servicoId));

        given()
                        .pathParam("id", servicoId)
                .when()
                        .delete("/servicos/{id}")
                .then()
                        .statusCode(204);

        given()
                        .pathParam("id", servicoId)
                .when()
                        .get("/servicos/{id}")
                .then()
                        .statusCode(500)
                        .body("message", equalTo("Servico nao encontrado."));
    }
}