package br.com.fiap.oficina.veiculo.application.usecases;

import br.com.fiap.oficina.veiculo.application.dtos.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;

public interface AtualizarVeiculoUseCase {
        Veiculo atualizar(AtualizarVeiculoCommand command);
}
