package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.domain.model.Veiculo;

import java.util.List;

public interface ConsultarTodosVeiculosUseCase {
    List<Veiculo> consultarTodos();
}