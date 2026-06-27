package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public interface ExcluirVeiculoUseCase {
    void excluir(VeiculoId veiculoId);
}