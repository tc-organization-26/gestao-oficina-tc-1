package br.com.fiap.oficina.cliente.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AtualizarClienteRequestTest {
    @Test
    void guardaDados() {
        var request = new AtualizarClienteRequest("Maria", "maria@email.com", "11");

        assertEquals("Maria", request.nome());
        assertEquals("11", request.telefone());
    }
}