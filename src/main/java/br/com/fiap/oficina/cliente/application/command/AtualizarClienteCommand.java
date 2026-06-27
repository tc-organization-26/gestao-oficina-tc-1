package br.com.fiap.oficina.cliente.application.command;

import java.util.UUID;

public record AtualizarClienteCommand(
        UUID clienteId,
        String nome,
        String email,
        String telefone
) {
}