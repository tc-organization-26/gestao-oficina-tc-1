package br.com.fiap.oficina.veiculo.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CadastrarVeiculoCommandTest {
    @Test
    void guardaDados() {
        var clienteId = UUID.randomUUID();
        var command = new CadastrarVeiculoCommand(clienteId, "ABC1D23", "Toyota", "Corolla", 2020);

        assertEquals(clienteId, command.clienteId());
        assertEquals("ABC1D23", command.placa());
    }
}