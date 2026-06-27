package br.com.fiap.oficina.veiculo.adapter.in.rest.response;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;
import org.junit.jupiter.api.Test;

class VeiculoResponseTest {
    @Test
    void criaResponseAPartirDoDominio() {
        var clienteId = ClienteId.novo();
        var veiculo = Veiculo.criar(clienteId, VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);

        var response = VeiculoResponse.from(veiculo);

        assertEquals(veiculo.id().value(), response.id());
        assertEquals(clienteId.value(), response.clienteId());
        assertEquals("ABC1D23", response.placa());
    }
}