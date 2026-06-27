package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.application.command.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;

public interface CadastrarVeiculoUseCase {
    Veiculo cadastrar(CadastrarVeiculoCommand command);
}
