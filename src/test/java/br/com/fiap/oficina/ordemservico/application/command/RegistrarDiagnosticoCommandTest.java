package br.com.fiap.oficina.ordemservico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegistrarDiagnosticoCommandTest {
    @Test
    void recordExpoeCampos() {
        var ordemServicoId = UUID.randomUUID();
        var command = new RegistrarDiagnosticoCommand(ordemServicoId, "Trocar vela");

        assertEquals(ordemServicoId, command.ordemServicoId());
        assertEquals("Trocar vela", command.descricao());
    }
}
