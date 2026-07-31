package br.com.fiap.oficina.cliente.application.usecases;

import br.com.fiap.oficina.cliente.domain.entities.Cliente;

import java.util.List;

public interface ConsultarTodosClientesUseCase {
    List<Cliente> consultarTodos();
}