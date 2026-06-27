package br.com.fiap.oficina.veiculo.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.shared.domain.DomainException;
import org.junit.jupiter.api.Test;

class VeiculoTest {
    @Test
    void criarPreencheDadosPadrao() {
        var clienteId = ClienteId.novo();
        var veiculo = Veiculo.criar(clienteId, VeiculoPlaca.novo("ABC1D23"), " Toyota ", " Corolla ", 2020);

        assertNotNull(veiculo.id());
        assertEquals(clienteId, veiculo.clienteId());
        assertEquals("Toyota", veiculo.marca());
        assertEquals("Corolla", veiculo.modelo());
        assertNotNull(veiculo.criadoEm());
    }

    @Test
    void atualizarAlteraDadosPermitidos() {
        var veiculo = Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);

        veiculo.atualizar("Honda", "Civic", 2021);

        assertEquals("Honda", veiculo.marca());
        assertEquals("Civic", veiculo.modelo());
        assertEquals(2021, veiculo.ano());
    }

    @Test
    void rejeitaAnoInvalido() {
        assertThrows(DomainException.class, () -> Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 0));
    }
}