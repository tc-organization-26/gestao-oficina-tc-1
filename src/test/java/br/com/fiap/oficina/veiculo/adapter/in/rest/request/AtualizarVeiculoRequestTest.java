package br.com.fiap.oficina.veiculo.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AtualizarVeiculoRequestTest {
    @Test
    void guardaDados() {
        var request = new AtualizarVeiculoRequest("Corolla", "Toyota", 2020);

        assertEquals("Corolla", request.modelo());
        assertEquals("Toyota", request.marca());
    }
}