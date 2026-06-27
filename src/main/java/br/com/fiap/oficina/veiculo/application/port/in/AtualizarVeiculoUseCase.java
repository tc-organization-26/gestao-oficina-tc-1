package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.application.command.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;

public interface AtualizarVeiculoUseCase {
        Veiculo atualizar(AtualizarVeiculoCommand command);
}
