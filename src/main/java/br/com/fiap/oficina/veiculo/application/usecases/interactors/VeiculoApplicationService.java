package br.com.fiap.oficina.veiculo.application.usecases.interactors;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.veiculo.application.dtos.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.dtos.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.dtos.ConsultarVeiculosPorClienteCommand;
import br.com.fiap.oficina.veiculo.application.usecases.AtualizarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.usecases.CadastrarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.usecases.ConsultarTodosVeiculosUseCase;
import br.com.fiap.oficina.veiculo.application.usecases.ConsultarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.usecases.ConsultarVeiculosPorClienteUseCase;
import br.com.fiap.oficina.veiculo.application.usecases.ExcluirVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.gateways.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoPlaca;

import java.util.List;

public class VeiculoApplicationService implements CadastrarVeiculoUseCase,
        AtualizarVeiculoUseCase,
        ConsultarVeiculoUseCase,
        ConsultarTodosVeiculosUseCase,
        ConsultarVeiculosPorClienteUseCase,
        ExcluirVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public VeiculoApplicationService(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Veiculo cadastrar(CadastrarVeiculoCommand command) {
        var placa = VeiculoPlaca.novo(command.placa());

        if (veiculoRepository.existePorPlaca(placa.value())) {
            throw new DomainException("Placa já cadastrada.");
        }

        var veiculo = Veiculo.criar(
                new ClienteId(command.clienteId()),
                placa,
                command.marca(),
                command.modelo(),
                command.ano());

        return veiculoRepository.salvar(veiculo);
    }
    @Override
    public Veiculo atualizar(AtualizarVeiculoCommand command) {
        VeiculoId veiculoId = new VeiculoId(command.veiculoId());

        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId)
                .orElseThrow(() -> new DomainException("Veiculo não encontrado."));

        veiculo.atualizar(
                command.marca(),
                command.modelo(),
                command.ano());

        return veiculoRepository.salvar(veiculo);
    }

    @Override
    public Veiculo consultarPorId(VeiculoId veiculoId) {
        return veiculoRepository.buscarPorId(veiculoId)
                .orElseThrow(() -> new DomainException("Veiculo não encontrado."));
    }

    @Override
    public List<Veiculo> consultarTodos() {
        return veiculoRepository.buscarTodos();
    }

    @Override
    public void excluir(VeiculoId veiculoId) {
        consultarPorId(veiculoId);
        veiculoRepository.excluirPorId(veiculoId);
    }

    @Override
    public List<Veiculo> consultarPorCliente(ConsultarVeiculosPorClienteCommand command) {
        return veiculoRepository.buscarPorClienteId(new ClienteId(command.clienteId()));
    }
}