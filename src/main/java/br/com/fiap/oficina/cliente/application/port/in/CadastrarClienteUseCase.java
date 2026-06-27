package br.com.fiap.oficina.cliente.application.port.in;

import br.com.fiap.oficina.cliente.application.command.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.domain.model.Cliente;

public interface CadastrarClienteUseCase {
    Cliente cadastrar(CadastrarClienteCommand command);
}
