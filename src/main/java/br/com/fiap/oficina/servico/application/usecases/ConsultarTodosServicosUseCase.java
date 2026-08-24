package br.com.fiap.oficina.servico.application.usecases;

import br.com.fiap.oficina.servico.domain.entities.Servico;

import java.util.List;

public interface ConsultarTodosServicosUseCase {
    List<Servico> consultarTodos();
}