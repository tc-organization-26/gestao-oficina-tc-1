package br.com.fiap.oficina.veiculo.application.usecases;

import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

public interface ConsultarVeiculoUseCase {
    Veiculo consultarPorId(VeiculoId id);
}
