package br.com.fiap.oficina.estoque.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AtualizarItemEstoqueCommandTest {
    @Test
    void recordExpoeCampos() {
        var id = UUID.randomUUID();
        var command = new AtualizarItemEstoqueCommand(id, "Filtro", BigDecimal.TEN);

        assertEquals(id, command.itemEstoqueId());
        assertEquals("Filtro", command.descricao());
        assertEquals(BigDecimal.TEN, command.valorUnitario());
    }
}