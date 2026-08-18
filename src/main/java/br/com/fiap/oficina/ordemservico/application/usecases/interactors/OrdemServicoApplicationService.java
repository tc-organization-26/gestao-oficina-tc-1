package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.ordemservico.application.dtos.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.application.usecases.AtualizarStatusOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.EntregarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FinalizarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.PedirAjusteOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarPagamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.PublicadorEventoGateway;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.entities.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.events.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.events.OrdemServicoFinalizadaEvent;
import br.com.fiap.oficina.ordemservico.domain.entities.Diagnostico;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class OrdemServicoApplicationService implements
        CriarOrdemServicoUseCase,
        ConsultarOrdemServicoUseCase,
        IniciarDiagnosticoUseCase,
        RegistrarDiagnosticoUseCase,
        IniciarExecucaoUseCase,
        FinalizarExecucaoUseCase,
        RegistrarPagamentoUseCase,
        EntregarOrdemServicoUseCase,
        PedirAjusteOrcamentoUseCase,
        AtualizarStatusOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;
    private final OrcamentoGateway orcamentoGateway;
    private final ServicoGateway servicoGateway;
    private final EstoqueGateway estoqueGateway;
    private final VerificadorEstoqueGateway verificadorEstoqueGateway;
    private final PublicadorEventoGateway publicadorEventoGateway;

    public OrdemServicoApplicationService(
            OrdemServicoGateway ordemServicoGateway,
            OrcamentoGateway orcamentoGateway,
            ServicoGateway servicoGateway,
            EstoqueGateway estoqueGateway,
            VerificadorEstoqueGateway verificadorEstoqueGateway,
            PublicadorEventoGateway publicadorEventoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.orcamentoGateway = orcamentoGateway;
        this.servicoGateway = servicoGateway;
        this.estoqueGateway = estoqueGateway;
        this.verificadorEstoqueGateway = verificadorEstoqueGateway;
        this.publicadorEventoGateway = publicadorEventoGateway;
    }

    @Override
    public OrdemServico criar(CriarOrdemServicoCommand command) {
        var ordemServico = OrdemServico.criar(
                new ClienteId(command.clienteId()),
                new VeiculoId(command.veiculoId()),
                command.anotacoes());
        var ordemSalva = ordemServicoGateway.salvar(ordemServico);
        var orcamento = ordemServico.orcamento();
        command.servicos().forEach(item -> {
            var servico = servicoGateway.buscarPorCodigo(item.codigo().trim().toUpperCase())
                    .orElseThrow(() -> new DomainException("Servico nao encontrado: " + item.codigo()));
            orcamento.adicionarItemServico(new OrcamentoItemServico(servico.id(), item.quantidade()));
        });
        command.pecas().forEach(item -> {
            var itemEstoque = estoqueGateway.buscarPorCodigo(item.codigo().trim().toUpperCase())
                    .orElseThrow(() -> new DomainException("Item de estoque nao encontrado: " + item.codigo()));
            orcamento.adicionarItemPeca(new ItemPeca(itemEstoque.id(), item.quantidade()));
            if (!verificadorEstoqueGateway.temTodosOsItensDisponiveis(orcamento.itensPeca())) {
                publicadorEventoGateway.publicar(new FaltaPecaEstoqueEvent(
                        ordemServico.id().value(),
                        itemEstoque.id().value(),
                        item.quantidade()));
            }
        });
        orcamentoGateway.salvar(orcamento);
        return ordemServicoGateway.buscarPorId(ordemSalva.id()).orElse(ordemSalva);
    }

    @Override
    public OrdemServico consultarPorId(OrdemServicoId ordemServicoId) {
        return buscarOrdemServico(ordemServicoId);
    }

    @Override
    public List<OrdemServico> consultarPorCliente(UUID clienteId) {
        return ordenarOrdensAtivas(ordemServicoGateway.buscarPorClienteOrdenado(clienteId));
    }

    @Override
    public List<OrdemServico> consultarOrdens(StatusOrdemServico status) {
        return ordenarOrdensAtivas(ordemServicoGateway.buscarTodosOrdenado()).stream()
                .filter(ordem -> status == null || ordem.status() == status)
                .toList();
    }

    @Override
    public String consultarTempoMedioExecucao() {
        var tempos = ordemServicoGateway.buscarTodosOrdenado().stream()
                .filter(ordem -> ordem.dataRecebimento() != null && ordem.finalizadaEm() != null)
                .map(ordem -> Duration.between(ordem.dataRecebimento(), ordem.finalizadaEm()).toSeconds())
                .toList();
        if (tempos.isEmpty()) {
            return "0:00";
        }
        var mediaSegundos = Math.round(tempos.stream().mapToLong(Long::longValue).average().orElse(0));
        var horas = mediaSegundos / 3600;
        var minutos = (mediaSegundos % 3600) / 60;
        return "%d:%02d".formatted(horas, minutos);
    }

    @Override
    public OrdemServico iniciarDiagnostico(OrdemServicoId ordemServicoId) {
        var ordemServico = buscarOrdemServico(ordemServicoId);
        ordemServico.iniciarDiagnostico();
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico registrarDiagnostico(RegistrarDiagnosticoCommand command) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(command.ordemServicoId()));
        ordemServico.registrarDiagnostico(Diagnostico.registrar(command.descricao()));
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico iniciarExecucao(UUID ordemId) {
        var ordemServicoId = new OrdemServicoId(ordemId);
        var ordemServico = buscarOrdemServico(ordemServicoId);
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + ordemId));
        if (orcamento.status() != StatusOrcamento.APROVADO) {
            throw new DomainException("Orcamento deve estar APROVADO para iniciar execucao. Status atual: " + orcamento.status());
        }
        ordemServico.iniciarExecucao();
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico finalizar(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.finalizar();
        var saved = ordemServicoGateway.salvar(ordemServico);
        publicadorEventoGateway.publicar(new OrdemServicoFinalizadaEvent(ordemServico.id().value(), ordemServico.clienteId().value()));
        return saved;
    }

    @Override
    public OrdemServico registrarPagamento(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.registrarPagamento();
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico entregar(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.entregar();
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico pedirAjuste(UUID ordemId) {
        var ordemServicoId = new OrdemServicoId(ordemId);
        var ordemServico = buscarOrdemServico(ordemServicoId);
        ordemServico.pedirAjuste();
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + ordemId));
        orcamento.reabrir();
        orcamentoGateway.salvar(orcamento);
        return ordemServicoGateway.salvar(ordemServico);
    }

    @Override
    public OrdemServico atualizarStatus(UUID ordemId, StatusOrdemServico status) {
        if (status == null) {
            throw new DomainException("Status da ordem de servico e obrigatorio.");
        }
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        if (ordemServico.status() == status) {
            return ordemServico;
        }
        return switch (status) {
            case EM_DIAGNOSTICO -> iniciarDiagnostico(ordemServico.id());
            case AGUARDANDO_APROVACAO -> fecharOrdemParaAprovacao(ordemServico);
            case EM_EXECUCAO -> iniciarExecucao(ordemId);
            case FINALIZADA -> finalizar(ordemId);
            case ENTREGUE -> entregar(ordemId);
            case RECEBIDA -> throw new DomainException("Nao e possivel retornar uma ordem de servico para RECEBIDA.");
        };
    }

    private OrdemServico buscarOrdemServico(OrdemServicoId ordemServicoId) {
        return ordemServicoGateway.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada."));
    }

    private OrdemServico fecharOrdemParaAprovacao(OrdemServico ordemServico) {
        var orcamento = orcamentoGateway.buscarPorOrdemServicoId(ordemServico.id())
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + ordemServico.id().value()));
        if (orcamento.status() != StatusOrcamento.ENVIADO) {
            orcamento.fechar();
            orcamentoGateway.salvar(orcamento);
        }
        ordemServico.finalizarOrcamento();
        return ordemServicoGateway.salvar(ordemServico);
    }

    private List<OrdemServico> ordenarOrdensAtivas(List<OrdemServico> ordens) {
        return ordens.stream()
                .filter(ordem -> ordem.status().ativaNaOficina())
                .sorted(Comparator
                        .comparingInt((OrdemServico ordem) -> ordem.status().prioridadeListagem())
                        .thenComparing(OrdemServico::dataRecebimento))
                .toList();
    }
}
