package br.com.fiap.oficina.ordemservico.application.service;

import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RecusarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.VerificadorEstoquePort;
import br.com.fiap.oficina.ordemservico.domain.event.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.event.OrcamentoFechadoEvent;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.model.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrcamentoApplicationService implements
    AdicionarItemPecaOrcamentoUseCase,
        AdicionarItemServicoOrcamentoUseCase,
        FecharOrcamentoUseCase,
        AprovarOrcamentoUseCase,
        RecusarOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepository;
    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final VerificadorEstoquePort verificadorEstoque;
    private final ApplicationEventPublisher eventPublisher;

    public OrcamentoApplicationService(
            OrcamentoRepositoryPort orcamentoRepository,
            OrdemServicoRepositoryPort ordemServicoRepository,
            VerificadorEstoquePort verificadorEstoque,
            ApplicationEventPublisher eventPublisher) {
        this.orcamentoRepository = orcamentoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.verificadorEstoque = verificadorEstoque;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command) {
        var orcamentoId = OrcamentoId.from(command.orcamentoId());
        var ordemServicoId = new OrdemServicoId(command.orcamentoId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
                .orElseGet(() -> new Orcamento(command.orcamentoId(), ordemServicoId));
        var item = new OrcamentoItemServico(new ServicoId(command.servicoId()), command.quantidade());
        orcamento.adicionarItemServico(item);

        if (verificadorEstoque.temTodosOsItensDisponiveis(orcamento.itensPeca())) {
            if (!orcamento.itensPeca().isEmpty()) {
                orcamento.fechar();
            }
        } else {
            orcamento.marcarParaVerificacaoEstoque();
        }

        return orcamentoRepository.salvar(orcamento);
    }

    @Override
    public Orcamento adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command) {
        var orcamentoId = OrcamentoId.from(command.orcamentoId());
        var ordemServicoId = new OrdemServicoId(command.orcamentoId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
                .orElseGet(() -> new Orcamento(command.orcamentoId(), ordemServicoId));
        var item = new ItemPeca(new ItemEstoqueId(command.itemEstoqueId()), command.quantidade());
        orcamento.adicionarItemPeca(item);

        if (verificadorEstoque.temTodosOsItensDisponiveis(orcamento.itensPeca())) {
            if (!orcamento.itensPeca().isEmpty()) {
                orcamento.fechar();
            }
        } else {
            orcamento.marcarParaVerificacaoEstoque();
            eventPublisher.publishEvent(new FaltaPecaEstoqueEvent(
                    command.orcamentoId(),
                    command.itemEstoqueId(),
                    command.quantidade()));
        }

        return orcamentoRepository.salvar(orcamento);
    }

    @Override
    public void fechar(FecharOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var ordemServico = ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + command.ordemId()));

        var orcamentoId = OrcamentoId.from(command.ordemId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + command.ordemId()));

        orcamento.fechar();
        orcamentoRepository.salvar(orcamento);

        ordemServico.finalizarOrcamento();
        ordemServicoRepository.salvar(ordemServico);

        eventPublisher.publishEvent(new OrcamentoFechadoEvent(ordemServico.id().value(), ordemServico.clienteId().value()));
    }

    @Override
    public OrdemServico aprovar(UUID ordemId) {
        var orcamento = orcamentoRepository.buscarPorId(OrcamentoId.from(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.aprovar();
        orcamentoRepository.salvar(orcamento);
        return ordemServicoRepository.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }

    @Override
    public OrdemServico recusar(UUID ordemId) {
        var orcamento = orcamentoRepository.buscarPorId(OrcamentoId.from(ordemId))
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + ordemId));
        orcamento.recusar();
        orcamentoRepository.salvar(orcamento);
        return ordemServicoRepository.buscarPorId(new OrdemServicoId(ordemId))
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + ordemId));
    }
}
