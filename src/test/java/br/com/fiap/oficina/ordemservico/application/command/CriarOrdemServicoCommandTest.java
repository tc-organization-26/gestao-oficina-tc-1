package br.com.fiap.oficina.ordemservico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CriarOrdemServicoCommandTest {
    @Test
    void recordExpoeCampos() {
        var clienteId = UUID.randomUUID();
        var veiculoId = UUID.randomUUID();
        var command = new CriarOrdemServicoCommand(clienteId, veiculoId, "Barulho no motor");

        assertEquals(clienteId, command.clienteId());
        assertEquals(veiculoId, command.veiculoId());
        assertEquals("Barulho no motor", command.anotacoes());
    }
}
