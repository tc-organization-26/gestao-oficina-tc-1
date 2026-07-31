package br.com.fiap.oficina.cliente.application.usecases;

import br.com.fiap.oficina.cliente.application.dtos.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.domain.entities.Cliente;

public interface CadastrarClienteUseCase {
    Cliente cadastrar(CadastrarClienteCommand command);
}
