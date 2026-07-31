package br.com.fiap.oficina.veiculo.application.usecases;

import br.com.fiap.oficina.veiculo.application.dtos.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;

public interface CadastrarVeiculoUseCase {
    Veiculo cadastrar(CadastrarVeiculoCommand command);
}
