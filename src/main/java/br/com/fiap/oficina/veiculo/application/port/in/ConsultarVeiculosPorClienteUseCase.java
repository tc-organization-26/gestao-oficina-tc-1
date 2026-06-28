package br.com.fiap.oficina.veiculo.application.port.in;

import br.com.fiap.oficina.veiculo.application.command.ConsultarVeiculosPorClienteCommand;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;

import java.util.List;

public interface ConsultarVeiculosPorClienteUseCase {
    List<Veiculo> consultarPorCliente(ConsultarVeiculosPorClienteCommand command);
}
