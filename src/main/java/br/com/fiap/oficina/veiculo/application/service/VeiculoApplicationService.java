package br.com.fiap.oficina.veiculo.application.service;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.application.command.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.command.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.port.in.AtualizarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.CadastrarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarTodosVeiculosUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ExcluirVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.out.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;

import java.util.List;

public class VeiculoApplicationService implements CadastrarVeiculoUseCase,
        AtualizarVeiculoUseCase,
        ConsultarVeiculoUseCase,
        ConsultarTodosVeiculosUseCase,
        ExcluirVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public VeiculoApplicationService(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Veiculo cadastrar(CadastrarVeiculoCommand command) {
        if (veiculoRepository.existePorPlaca(command.placa())) {
            throw new DomainException("Placa já cadastrada.");
        }

        var veiculo = Veiculo.criar(
                new ClienteId(command.clienteId()),
                VeiculoPlaca.novo(command.placa()),
                command.marca(),
                command.modelo(),
                command.ano()
        );

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
                command.ano()
        );

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
}