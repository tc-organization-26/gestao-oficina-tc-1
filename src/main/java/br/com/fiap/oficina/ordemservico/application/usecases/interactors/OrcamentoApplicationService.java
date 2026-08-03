package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RecusarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.PublicadorEventoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.domain.events.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.events.OrcamentoFechadoEvent;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.entities.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.UUID;

public class OrcamentoApplicationService implements
        AdicionarItemPecaOrcamentoUseCase,
        AdicionarItemServicoOrcamentoUseCase,
        FecharOrcamentoUseCase,
        AprovarOrcamentoUseCase,
        RecusarOrcamentoUseCase {

    private final OrcamentoGateway orcamentoGateway;
    private final OrdemServicoGateway ordemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final EstoqueGateway estoqueGateway;
    private final VerificadorEstoqueGateway verificadorEstoque;
    private final PublicadorEventoGateway publicadorEventoGateway;

    public OrcamentoApplicationService(
            OrcamentoGateway orcamentoGateway,
            OrdemServicoGateway ordemServicoGateway,
            ServicoGateway servicoGateway,
            EstoqueGateway estoqueGateway,
            VerificadorEstoqueGateway verificadorEstoque,
            PublicadorEventoGateway publicadorEventoGateway) {
        this.orcamentoGateway = orcamentoGateway;
        this.ordemServicoGateway = ordemServicoGateway;
        this.servicoGateway = servicoGateway;
        this.estoqueGateway = estoqueGateway;
        this.verificadorEstoque = verificadorEstoque;
        this.publicadorEventoGateway = publicadorEventoGateway;
    }

    @Override
    public OrdemServico adicionarItemServico(AdicionarItemServicoOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + command.ordemId()));
        var servico = servicoGateway.buscarPorCodigo(command.servicoCodigo().trim().toUpperCase())
                .orElseThrow(() -> new DomainException("Servico nao encontrado: " + command.servicoCodigo()));
        var item = new OrcamentoItemServico(servico.id(), command.quantidade());
        orcamento.adicionarItemServico(item);
        orcamentoGateway.salvar(orcamento);
        return buscarOrdemServicoAtualizada(ordemServicoId);
    }

    @Override
    public OrdemServico adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + command.ordemId()));
        var itemEstoque = estoqueGateway.buscarPorCodigo(command.itemEstoqueCodigo().trim().toUpperCase())
                .orElseThrow(() -> new DomainException("Item de estoque nao encontrado: " + command.itemEstoqueCodigo()));
        var item = new ItemPeca(itemEstoque.id(), command.quantidade());
        orcamento.adicionarItemPeca(item);
        if (!verificadorEstoque.temTodosOsItensDisponiveis(orcamento.itensPeca())) {
            publicadorEventoGateway.publicar(new FaltaPecaEstoqueEvent(
                    command.ordemId(),
                    itemEstoque.id().value(),
                    command.quantidade()));
        }

        orcamentoGateway.salvar(orcamento);
        return buscarOrdemServicoAtualizada(ordemServicoId);
    }

    @Override
    public OrdemServico fechar(FecharOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var ordemServico = ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + command.ordemId()));

        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + command.ordemId()));

        fecharOrcamentoComSucesso(ordemServico, orcamento);
        return buscarOrdemServicoAtualizada(ordemServicoId);
    }

    private OrdemServico buscarOrdemServicoAtualizada(OrdemServicoId ordemServicoId) {
        return ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemServicoId.value()));
    }

    private Orcamento fecharOrcamentoComSucesso(OrdemServico ordemServico, Orcamento orcamento) {
        if (orcamento.status() != StatusOrcamento.ENVIADO) {
            orcamento.fechar();
        }
        var orcamentoSalvo = orcamentoGateway.salvar(orcamento);

        ordemServico.finalizarOrcamento();
        ordemServicoGateway.salvar(ordemServico);

        publicadorEventoGateway.publicar(new OrcamentoFechadoEvent(ordemServico.id().value(), ordemServico.clienteId().value()));
        return orcamentoSalvo;
    }

    @Override
    public OrdemServico aprovar(UUID ordemId) {
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.aprovar();
        orcamentoGateway.salvar(orcamento);
        return ordemServicoGateway.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }

    @Override
    public OrdemServico recusar(UUID ordemId) {
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.recusar();
        orcamentoGateway.salvar(orcamento);
        return ordemServicoGateway.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }
}