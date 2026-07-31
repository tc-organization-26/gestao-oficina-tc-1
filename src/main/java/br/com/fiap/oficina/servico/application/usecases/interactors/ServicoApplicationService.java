package br.com.fiap.oficina.servico.application.usecases.interactors;

import br.com.fiap.oficina.servico.application.dtos.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.application.dtos.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.application.usecases.AtualizarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.CadastrarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.ConsultarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.ConsultarTodosServicosUseCase;
import br.com.fiap.oficina.servico.application.usecases.ExcluirServicoUseCase;
import br.com.fiap.oficina.servico.application.gateways.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.List;

public class ServicoApplicationService implements CadastrarServicoUseCase,
        AtualizarServicoUseCase,
        ConsultarServicoUseCase,
        ConsultarTodosServicosUseCase,
        ExcluirServicoUseCase {

    private final ServicoRepositoryPort servicoRepository;

    public ServicoApplicationService(ServicoRepositoryPort servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    public Servico cadastrar(CadastrarServicoCommand command) {
        if (servicoRepository.existePorCodigo(command.codigo())) {
            throw new DomainException("Código de serviço já cadastrado.");
        }

        var servico = Servico.criar(
                command.codigo(),
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos());

        return servicoRepository.salvar(servico);
    }

    @Override
    public Servico atualizar(AtualizarServicoCommand command) {
        ServicoId servicoId = new ServicoId(command.servicoId());

        Servico servico = servicoRepository.buscarPorId(servicoId)
                .orElseThrow(() -> new DomainException("Servico não encontrado."));

        servico.atualizar(
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos()
        );

        return servicoRepository.salvar(servico);
    }

    @Override
    public Servico consultarPorId(ServicoId servicoId) {
        return servicoRepository.buscarPorId(servicoId)
                .orElseThrow(() -> new DomainException("Servico não encontrado."));
    }

    @Override
    public List<Servico> consultarTodos() {
        return servicoRepository.buscarTodos();
    }

    @Override
    public void excluir(ServicoId servicoId) {
        consultarPorId(servicoId);
        servicoRepository.excluirPorId(servicoId);
    }
}