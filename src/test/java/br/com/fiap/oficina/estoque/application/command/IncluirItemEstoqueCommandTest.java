package br.com.fiap.oficina.estoque.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IncluirItemEstoqueCommandTest {
    @Test
    void recordExpoeCampos() {
        var id = UUID.randomUUID();
        var command = new IncluirItemEstoqueCommand(id, BigDecimal.ONE);

        assertEquals(id, command.itemEstoqueId());
        assertEquals(BigDecimal.ONE, command.quantidade());
    }
}