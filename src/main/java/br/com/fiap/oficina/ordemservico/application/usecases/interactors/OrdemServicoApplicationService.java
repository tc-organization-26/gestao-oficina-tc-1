package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.ordemservico.application.dtos.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.usecases.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.EntregarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FinalizarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.PedirAjusteOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarPagamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.gateways.PublicarEventoPort;
import br.com.fiap.oficina.ordemservico.domain.events.OrdemServicoFinalizadaEvent;
import br.com.fiap.oficina.ordemservico.domain.entities.Diagnostico;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

import java.time.Duration;
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
        PedirAjusteOrcamentoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OrcamentoRepositoryPort orcamentoRepository;
    private final PublicarEventoPort publicarEvento;

    public OrdemServicoApplicationService(
            OrdemServicoRepositoryPort ordemServicoRepository,
            OrcamentoRepositoryPort orcamentoRepository,
            PublicarEventoPort publicarEvento) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.publicarEvento = publicarEvento;
    }

    @Override
    public OrdemServico criar(CriarOrdemServicoCommand command) {
        var ordemServico = OrdemServico.criar(
                new ClienteId(command.clienteId()),
                new VeiculoId(command.veiculoId()),
                command.anotacoes());
        var ordemSalva = ordemServicoRepository.salvar(ordemServico);
        orcamentoRepository.salvar(ordemServico.orcamento());
        return ordemServicoRepository.buscarPorId(ordemSalva.id()).orElse(ordemSalva);
    }

    @Override
    public OrdemServico consultarPorId(OrdemServicoId ordemServicoId) {
        return buscarOrdemServico(ordemServicoId);
    }

    @Override
    public List<OrdemServico> consultarPorCliente(UUID clienteId) {
        return ordemServicoRepository.buscarPorClienteOrdenado(clienteId);
    }

    @Override
    public List<OrdemServico> consultarOrdens(StatusOrdemServico status) {
        if (status == null) {
            return ordemServicoRepository.buscarTodosOrdenado();
        }
        return ordemServicoRepository.buscarPorStatusOrdenado(status.ordinal());
    }

    @Override
    public String consultarTempoMedioExecucao() {
        var tempos = ordemServicoRepository.buscarTodosOrdenado().stream()
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
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico registrarDiagnostico(RegistrarDiagnosticoCommand command) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(command.ordemServicoId()));
        ordemServico.registrarDiagnostico(Diagnostico.registrar(command.descricao()));
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico iniciarExecucao(UUID ordemId) {
        var ordemServicoId = new OrdemServicoId(ordemId);
        var ordemServico = buscarOrdemServico(ordemServicoId);
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + ordemId));
        if (orcamento.status() != StatusOrcamento.APROVADO) {
            throw new DomainException("Orcamento deve estar APROVADO para iniciar execucao. Status atual: " + orcamento.status());
        }
        ordemServico.iniciarExecucao();
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico finalizar(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.finalizar();
        var saved = ordemServicoRepository.salvar(ordemServico);
        publicarEvento.publicar(new OrdemServicoFinalizadaEvent(ordemServico.id().value(), ordemServico.clienteId().value()));
        return saved;
    }

    @Override
    public OrdemServico registrarPagamento(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.registrarPagamento();
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico entregar(UUID ordemId) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(ordemId));
        ordemServico.entregar();
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico pedirAjuste(UUID ordemId) {
        var ordemServicoId = new OrdemServicoId(ordemId);
        var ordemServico = buscarOrdemServico(ordemServicoId);
        ordemServico.pedirAjuste();
        var orcamento = orcamentoRepository.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado para a ordem: " + ordemId));
        orcamento.reabrir();
        orcamentoRepository.salvar(orcamento);
        return ordemServicoRepository.salvar(ordemServico);
    }

    private OrdemServico buscarOrdemServico(OrdemServicoId ordemServicoId) {
        return ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada."));
    }
}
