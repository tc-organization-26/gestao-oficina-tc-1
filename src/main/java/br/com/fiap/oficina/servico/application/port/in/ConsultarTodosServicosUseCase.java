package br.com.fiap.oficina.servico.application.port.in;

import br.com.fiap.oficina.servico.domain.model.Servico;

import java.util.List;

public interface ConsultarTodosServicosUseCase {
    List<Servico> consultarTodos();
}