package br.com.fiap.oficina.estoque.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BaixarItemEstoqueCommandTest {
    @Test
    void recordExpoeCampos() {
        var command = new BaixarItemEstoqueCommand("OLEO", BigDecimal.ONE);

        assertEquals("OLEO", command.codigo());
        assertEquals(BigDecimal.ONE, command.quantidade());
    }
}