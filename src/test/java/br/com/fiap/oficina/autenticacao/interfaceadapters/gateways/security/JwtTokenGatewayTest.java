package br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security;

import br.com.fiap.oficina.autenticacao.domain.entities.Usuario;
import br.com.fiap.oficina.autenticacao.domain.enums.Papel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenGatewayTest {

    @Test
    void deveAceitarTokenComPapelCliente() {
        var gateway = new JwtTokenGateway("segredo-jwt-com-mais-de-trinta-e-dois-bytes", 3600);
        var cliente = Usuario.criar("52998224725", "nao-utilizada-na-autenticacao-por-cpf", Papel.CLIENTE);

        var token = gateway.gerarToken(cliente);
        var autenticado = gateway.validar(token);

        assertTrue(autenticado.isPresent());
        assertEquals(cliente.id().value(), autenticado.orElseThrow().usuarioId());
        assertEquals(Papel.CLIENTE, autenticado.orElseThrow().papel());
    }
}
