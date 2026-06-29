package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AuthApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveTentarAutenticarUsuario() {
        // given
        var credenciais = Map.of(
                "login", "admin",
                "senha", "ad@456");

        // when
        var resposta = postMap("/auth/login", credenciais);

        // then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertNotNull(resposta.getBody().get("token"));
    }
}
