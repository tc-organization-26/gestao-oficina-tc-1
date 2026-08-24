package br.com.fiap.oficina.servico.application.usecases.interactors;

import br.com.fiap.oficina.servico.application.dtos.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.application.dtos.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.application.usecases.AtualizarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.CadastrarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.ConsultarServicoUseCase;
import br.com.fiap.oficina.servico.application.usecases.ConsultarTodosServicosUseCase;
import br.com.fiap.oficina.servico.application.usecases.ExcluirServicoUseCase;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.List;

public class ServicoApplicationService implements CadastrarServicoUseCase,
        AtualizarServicoUseCase,
        ConsultarServicoUseCase,
        ConsultarTodosServicosUseCase,
        ExcluirServicoUseCase {

    private final ServicoGateway servicoGateway;

    public ServicoApplicationService(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    @Override
    public Servico cadastrar(CadastrarServicoCommand command) {
        if (servicoGateway.existePorCodigo(command.codigo())) {
            throw new DomainException("Código de serviço já cadastrado.");
        }

        var servico = Servico.criar(
                command.codigo(),
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos());

        return servicoGateway.salvar(servico);
    }

    @Override
    public Servico atualizar(AtualizarServicoCommand command) {
        ServicoId servicoId = new ServicoId(command.servicoId());

        Servico servico = servicoGateway.buscarPorId(servicoId)
                .orElseThrow(() -> new DomainException("Servico não encontrado."));

        servico.atualizar(
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos()
        );

        return servicoGateway.salvar(servico);
    }

    @Override
    public Servico consultarPorId(ServicoId servicoId) {
        return servicoGateway.buscarPorId(servicoId)
                .orElseThrow(() -> new DomainException("Servico não encontrado."));
    }

    @Override
    public List<Servico> consultarTodos() {
        return servicoGateway.buscarTodos();
    }

    @Override
    public void excluir(ServicoId servicoId) {
        consultarPorId(servicoId);
        servicoGateway.excluirPorId(servicoId);
    }
}