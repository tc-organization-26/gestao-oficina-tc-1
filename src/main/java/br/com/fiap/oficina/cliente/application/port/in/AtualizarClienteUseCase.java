package br.com.fiap.oficina.cliente.application.port.in;

import br.com.fiap.oficina.cliente.application.command.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.domain.model.Cliente;

public interface AtualizarClienteUseCase {

    Cliente atualizar(AtualizarClienteCommand command);
}