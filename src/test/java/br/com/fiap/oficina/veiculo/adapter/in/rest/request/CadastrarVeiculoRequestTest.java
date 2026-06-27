package br.com.fiap.oficina.veiculo.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CadastrarVeiculoRequestTest {
    @Test
    void guardaDados() {
        var request = new CadastrarVeiculoRequest("cliente-id", "ABC1D23", "Toyota", "Corolla", 2020);

        assertEquals("cliente-id", request.clienteId());
        assertEquals("ABC1D23", request.placa());
    }
}