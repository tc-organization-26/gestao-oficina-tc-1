package br.com.fiap.oficina.veiculo.application.gateways;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

public interface VeiculoGateway {

    boolean existePorPlaca(String placa);

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(VeiculoId veiculoId);

    List<Veiculo> buscarTodos();

    void excluirPorId(VeiculoId veiculoId);

    List<Veiculo> buscarPorClienteId(ClienteId clienteId);
}