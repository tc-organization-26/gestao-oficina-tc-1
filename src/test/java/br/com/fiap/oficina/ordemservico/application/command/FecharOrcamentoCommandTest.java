package br.com.fiap.oficina.ordemservico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class FecharOrcamentoCommandTest {
    @Test
    void recordExpoeCampos() {
        var ordemId = UUID.randomUUID();
        var command = new FecharOrcamentoCommand(ordemId);

        assertEquals(ordemId, command.ordemId());
    }
}
