package br.com.fiap.oficina.veiculo.application.usecases;

import br.com.fiap.oficina.veiculo.application.dtos.ConsultarVeiculosPorClienteCommand;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;

import java.util.List;

public interface ConsultarVeiculosPorClienteUseCase {
    List<Veiculo> consultarPorCliente(ConsultarVeiculosPorClienteCommand command);
}
