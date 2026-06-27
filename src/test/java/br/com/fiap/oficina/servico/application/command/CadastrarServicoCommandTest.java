package br.com.fiap.oficina.servico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CadastrarServicoCommandTest {
    @Test
    void guardaDados() {
        var command = new CadastrarServicoCommand("TROCA", "Troca", BigDecimal.TEN, 60);

        assertEquals("TROCA", command.codigo());
        assertEquals(BigDecimal.TEN, command.valorUnitario());
    }
}