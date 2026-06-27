package br.com.fiap.oficina.cliente.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AtualizarClienteCommandTest {
    @Test
    void guardaDados() {
        var id = UUID.randomUUID();
        var command = new AtualizarClienteCommand(id, "Maria", "maria@email.com", "11");

        assertEquals(id, command.clienteId());
        assertEquals("Maria", command.nome());
    }
}