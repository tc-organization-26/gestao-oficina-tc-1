package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public interface ConsultarVeiculoUseCase {
    Veiculo consultarPorId(VeiculoId id);
}
