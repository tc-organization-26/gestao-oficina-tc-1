package br.com.fiap.oficina.estoque.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CadastrarItemEstoqueCommandTest {
    @Test
    void recordExpoeCampos() {
        var command = new CadastrarItemEstoqueCommand("FILTRO", "Filtro de oleo", BigDecimal.TEN, BigDecimal.ONE);

        assertEquals("FILTRO", command.codigo());
        assertEquals("Filtro de oleo", command.descricao());
        assertEquals(BigDecimal.TEN, command.valorUnitario());
        assertEquals(BigDecimal.ONE, command.quantidadeInicial());
    }
}
