package br.com.fiap.oficina.veiculo.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public interface VeiculoRepositoryPort {

    boolean existePorPlaca(String placa);

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(VeiculoId veiculoId);

    List<Veiculo> buscarTodos();

    void excluirPorId(VeiculoId veiculoId);

    List<Veiculo> buscarPorClienteId(ClienteId clienteId);
}