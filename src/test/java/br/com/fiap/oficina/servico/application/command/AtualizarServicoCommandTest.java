package br.com.fiap.oficina.servico.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AtualizarServicoCommandTest {
    @Test
    void guardaDados() {
        var id = UUID.randomUUID();
        var command = new AtualizarServicoCommand(id, "Troca", BigDecimal.TEN, 60);

        assertEquals(id, command.servicoId());
        assertEquals("Troca", command.descricao());
    }
}