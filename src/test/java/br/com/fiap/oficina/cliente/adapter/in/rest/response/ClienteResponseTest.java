package br.com.fiap.oficina.cliente.adapter.in.rest.response;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;
import org.junit.jupiter.api.Test;

class ClienteResponseTest {
    @Test
    void criaResponseAPartirDoDominio() {
        var cliente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11");

        var response = ClienteResponse.from(cliente);

        assertEquals(cliente.id().value(), response.id());
        assertEquals("12345678901", response.cpfCnpj());
        assertEquals("Maria", response.nome());
    }
}