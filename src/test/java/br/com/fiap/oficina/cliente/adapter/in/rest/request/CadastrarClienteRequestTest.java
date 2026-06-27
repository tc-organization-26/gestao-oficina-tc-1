package br.com.fiap.oficina.cliente.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CadastrarClienteRequestTest {
    @Test
    void guardaDados() {
        var request = new CadastrarClienteRequest("Maria", "12345678901", "maria@email.com", "11");

        assertEquals("Maria", request.nome());
        assertEquals("12345678901", request.cpfCnpj());
    }
}