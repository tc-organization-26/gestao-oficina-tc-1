package br.com.fiap.oficina.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApiIntegrationSupport {

    private final RestTemplate restTemplate = new RestTemplate();

    protected AbstractApiIntegrationSupport() {
        restTemplate.setErrorHandler(response -> false);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        limparBanco();
    }

    @AfterEach
    void afterEach() {
        limparBanco();
    }

    protected ResponseEntity<Map> postMap(String path, Object body) {
        return restTemplate.postForEntity(url(path), body, Map.class);
    }

    protected ResponseEntity<Map> getMap(String path) {
        return restTemplate.getForEntity(url(path), Map.class);
    }

    protected ResponseEntity<List> getList(String path) {
        return restTemplate.getForEntity(url(path), List.class);
    }

    protected ResponseEntity<Void> delete(String path) {
        return restTemplate.exchange(url(path), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }

    protected void put(String path, Object body) {
        restTemplate.put(url(path), body);
    }

    protected void limparBanco() {
        jdbcTemplate.update("delete from orcamento_item_servico");
        jdbcTemplate.update("delete from orcamento");
        jdbcTemplate.update("delete from ordem_servico");
        jdbcTemplate.update("delete from veiculo");
        jdbcTemplate.update("delete from cliente");
        jdbcTemplate.update("delete from servico");
        jdbcTemplate.update("delete from item_estoque");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
