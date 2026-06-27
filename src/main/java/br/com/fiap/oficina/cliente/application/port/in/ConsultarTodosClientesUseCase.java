package br.com.fiap.oficina.cliente.application.port.in;

import br.com.fiap.oficina.cliente.domain.model.Cliente;

import java.util.List;

public interface ConsultarTodosClientesUseCase {
    List<Cliente> consultarTodos();
}