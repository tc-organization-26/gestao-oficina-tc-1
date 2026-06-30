package br.com.fiap.oficina.ordemservico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdicionarItemServicoOrcamentoCommandTest {
    @Test
    void recordExpoeCampos() {
        var ordemId = UUID.randomUUID();
        var command = new AdicionarItemServicoOrcamentoCommand(ordemId, "ALINHAMENTO", 1.0);

        assertEquals(ordemId, command.ordemId());
        assertEquals("ALINHAMENTO", command.servicoCodigo());
        assertEquals(1.0, command.quantidade());
    }
}
