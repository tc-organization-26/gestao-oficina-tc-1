package br.com.fiap.oficina.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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

    private String token;

    @BeforeEach
    void beforeEach() {
        limparBanco();
        token = null;
    }

    @AfterEach
    void afterEach() {
        limparBanco();
    }

    protected ResponseEntity<Map> postMap(String path, Object body) {
        if ("/auth/login".equals(path)) {
            return restTemplate.postForEntity(url(path), body, Map.class);
        }
        var headers = headersComAuth();
        return restTemplate.exchange(url(path), HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
    }

    protected ResponseEntity<Map> getMap(String path) {
        return restTemplate.exchange(url(path), HttpMethod.GET, new HttpEntity<>(headersComAuth()), Map.class);
    }

    protected ResponseEntity<List> getList(String path) {
        return restTemplate.exchange(url(path), HttpMethod.GET, new HttpEntity<>(headersComAuth()), List.class);
    }

    protected ResponseEntity<Void> delete(String path) {
        return restTemplate.exchange(url(path), HttpMethod.DELETE, new HttpEntity<>(headersComAuth()), Void.class);
    }

    protected void put(String path, Object body) {
        restTemplate.exchange(url(path), HttpMethod.PUT, new HttpEntity<>(body, headersComAuth()), Void.class);
    }

    private HttpHeaders headersComAuth() {
        if (token == null) {
            var credenciais = new HashMap<String, String>();
            credenciais.put("login", "admin");
            credenciais.put("senha", "ad@456");
            var login = restTemplate.postForEntity(url("/auth/login"), credenciais, Map.class);
            token = login.getBody().get("token").toString();
        }

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    protected void limparBanco() {
        jdbcTemplate.update("delete from movimentacao_estoque");
        jdbcTemplate.update("delete from orcamento_item_peca");
        jdbcTemplate.update("delete from orcamento_item_servico");
        jdbcTemplate.update("delete from orcamento");
        jdbcTemplate.update("delete from diagnostico");
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
