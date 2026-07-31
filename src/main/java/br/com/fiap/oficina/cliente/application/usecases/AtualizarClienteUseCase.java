package br.com.fiap.oficina.cliente.application.usecases;

import br.com.fiap.oficina.cliente.application.dtos.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.domain.entities.Cliente;

public interface AtualizarClienteUseCase {

    Cliente atualizar(AtualizarClienteCommand command);
}