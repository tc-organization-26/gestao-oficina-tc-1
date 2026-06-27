package br.com.fiap.oficina.cliente.application.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CadastrarClienteCommandTest {
    @Test
    void guardaDados() {
        var command = new CadastrarClienteCommand("Maria", "12345678901", "maria@email.com", "11");

        assertEquals("Maria", command.nome());
        assertEquals("12345678901", command.cpfCnpj());
        assertEquals("maria@email.com", command.email());
        assertEquals("11", command.telefone());
    }
}