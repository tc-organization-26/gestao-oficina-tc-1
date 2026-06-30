package br.com.fiap.oficina.ordemservico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdicionarItemPecaOrcamentoCommandTest {
    @Test
    void recordExpoeCampos() {
        var ordemId = UUID.randomUUID();
        var command = new AdicionarItemPecaOrcamentoCommand(ordemId, "FILTRO", 2.0);

        assertEquals(ordemId, command.ordemId());
        assertEquals("FILTRO", command.itemEstoqueCodigo());
        assertEquals(2.0, command.quantidade());
    }
}
