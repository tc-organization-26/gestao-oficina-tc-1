package br.com.fiap.oficina.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    protected JdbcTemplate jdbcTemplate;

    private String token;

    protected void resetToken() {
        token = null;
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

    protected void limparOrdens(Collection<String> ordemIds) {
        ordemIds.forEach(ordemId -> {
            var id = UUID.fromString(ordemId);
            jdbcTemplate.update("delete from orcamento_item_peca where orcamento_id in (select id from orcamento where ordem_servico_id = ?)", id);
            jdbcTemplate.update("delete from orcamento_item_servico where orcamento_id in (select id from orcamento where ordem_servico_id = ?)", id);
            jdbcTemplate.update("delete from orcamento where ordem_servico_id = ?", id);
            jdbcTemplate.update("delete from diagnostico where ordem_servico_id = ?", id);
            jdbcTemplate.update("delete from ordem_servico where id = ?", id);
        });
    }

    protected void limparVeiculos(Collection<String> veiculoIds) {
        veiculoIds.forEach(veiculoId -> jdbcTemplate.update("delete from veiculo where id = ?", UUID.fromString(veiculoId)));
    }

    protected void limparClientes(Collection<String> clienteIds) {
        clienteIds.forEach(clienteId -> jdbcTemplate.update("delete from cliente where id = ?", UUID.fromString(clienteId)));
    }

    protected void limparServicos(Collection<String> servicoIds) {
        servicoIds.forEach(servicoId -> jdbcTemplate.update("delete from servico where id = ?", UUID.fromString(servicoId)));
    }

    protected void limparItensEstoque(Collection<String> itemEstoqueIds) {
        itemEstoqueIds.forEach(itemId -> {
            var id = UUID.fromString(itemId);
            jdbcTemplate.update("delete from movimentacao_estoque where item_estoque_id = ?", id);
            jdbcTemplate.update("delete from orcamento_item_peca where item_estoque_id = ?", id);
            jdbcTemplate.update("delete from item_estoque where id = ?", id);
        });
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}

