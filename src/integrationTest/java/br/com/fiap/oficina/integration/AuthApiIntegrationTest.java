package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthApiIntegrationTest extends AbstractApiIntegrationSupport {

    @BeforeAll
    void beforeAll() {
        resetToken();
    }

    @BeforeEach
    void beforeEach() {
        resetToken();
    }

    @Test
    void deveTentarAutenticarUsuario() {
        var credenciais = Map.of(
                "login", "admin",
                "senha", "ad@456");

        var resposta = postMap("/auth/login", credenciais);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertNotNull(resposta.getBody().get("token"));
    }
}