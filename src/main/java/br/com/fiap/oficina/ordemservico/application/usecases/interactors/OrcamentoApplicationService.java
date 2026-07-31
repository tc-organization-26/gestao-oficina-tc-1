package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import br.com.fiap.oficina.estoque.application.gateways.EstoqueRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RecusarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.gateways.PublicarEventoPort;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoquePort;
import br.com.fiap.oficina.ordemservico.domain.events.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.events.OrcamentoFechadoEvent;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.entities.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import br.com.fiap.oficina.servico.application.gateways.ServicoRepositoryPort;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.UUID;

public class OrcamentoApplicationService implements
        AdicionarItemPecaOrcamentoUseCase,
        AdicionarItemServicoOrcamentoUseCase,
        FecharOrcamentoUseCase,
        AprovarOrcamentoUseCase,
        RecusarOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepository;
    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final ServicoRepositoryPort servicoRepository;
    private final EstoqueRepositoryPort estoqueRepository;
    private final VerificadorEstoquePort verificadorEstoque;
    private final PublicarEventoPort publicarEvento;

    public OrcamentoApplicationService(
            OrcamentoRepositoryPort orcamentoRepository,
            OrdemServicoRepositoryPort ordemServicoRepository,
            ServicoRepositoryPort servicoRepository,
            EstoqueRepositoryPort estoqueRepository,
            VerificadorEstoquePort verificadorEstoque,
            PublicarEventoPort publicarEvento) {
        this.orcamentoRepository = orcamentoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.servicoRepository = servicoRepository;
        this.estoqueRepository = estoqueRepository;
        this.verificadorEstoque = verificadorEstoque;
        this.publicarEvento = publicarEvento;
    }

    @Override
    public Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + command.ordemId()));
        var servico = servicoRepository.buscarPorCodigo(command.servicoCodigo().trim().toUpperCase())
                .orElseThrow(() -> new DomainException("Servico nao encontrado: " + command.servicoCodigo()));
        var item = new OrcamentoItemServico(servico.id(), command.quantidade());
        orcamento.adicionarItemServico(item);
        return orcamentoRepository.salvar(orcamento);
    }

    @Override
    public Orcamento adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + command.ordemId()));
        var itemEstoque = estoqueRepository.buscarPorCodigo(command.itemEstoqueCodigo().trim().toUpperCase())
                .orElseThrow(() -> new DomainException("Item de estoque nao encontrado: " + command.itemEstoqueCodigo()));
        var item = new ItemPeca(itemEstoque.id(), command.quantidade());
        orcamento.adicionarItemPeca(item);
        if (!verificadorEstoque.temTodosOsItensDisponiveis(orcamento.itensPeca())) {
            publicarEvento.publicar(new FaltaPecaEstoqueEvent(
                    command.ordemId(),
                    itemEstoque.id().value(),
                    command.quantidade()));
        }

        return orcamentoRepository.salvar(orcamento);
    }

    @Override
    public void fechar(FecharOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var ordemServico = ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + command.ordemId()));

        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + command.ordemId()));

        fecharOrcamentoComSucesso(ordemServico, orcamento);
    }

    private Orcamento fecharOrcamentoComSucesso(OrdemServico ordemServico, Orcamento orcamento) {
        if (orcamento.status() != StatusOrcamento.ENVIADO) {
            orcamento.fechar();
        }
        var orcamentoSalvo = orcamentoRepository.salvar(orcamento);

        ordemServico.finalizarOrcamento();
        ordemServicoRepository.salvar(ordemServico);

        publicarEvento.publicar(new OrcamentoFechadoEvent(ordemServico.id().value(), ordemServico.clienteId().value()));
        return orcamentoSalvo;
    }

    @Override
    public OrdemServico aprovar(UUID ordemId) {
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.aprovar();
        orcamentoRepository.salvar(orcamento);
        return ordemServicoRepository.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }

    @Override
    public OrdemServico recusar(UUID ordemId) {
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.recusar();
        orcamentoRepository.salvar(orcamento);
        return ordemServicoRepository.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }
}