package br.com.fiap.oficina.veiculo.application.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AtualizarVeiculoCommandTest {
    @Test
    void guardaDados() {
        var id = UUID.randomUUID();
        var command = new AtualizarVeiculoCommand(id, "Toyota", "Corolla", 2020);

        assertEquals(id, command.veiculoId());
        assertEquals("Toyota", command.marca());
        assertEquals("Corolla", command.modelo());
    }
}