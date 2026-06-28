package br.com.fiap.oficina.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApiIntegrationSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost";
    }

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
        limparBanco();
    }

    @AfterEach
    void afterEach() {
        limparBanco();
    }

    @AfterAll
    static void afterAll() {
        RestAssured.reset();
    }

    protected void limparBanco() {
        jdbcTemplate.update("delete from ordem_servico");
        jdbcTemplate.update("delete from veiculo");
        jdbcTemplate.update("delete from cliente");
        jdbcTemplate.update("delete from servico");
        jdbcTemplate.update("delete from item_estoque");
    }
}