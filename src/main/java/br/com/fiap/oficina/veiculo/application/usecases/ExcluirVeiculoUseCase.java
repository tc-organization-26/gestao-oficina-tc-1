package br.com.fiap.oficina.veiculo.application.usecases;

import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

public interface ExcluirVeiculoUseCase {
    void excluir(VeiculoId veiculoId);
}