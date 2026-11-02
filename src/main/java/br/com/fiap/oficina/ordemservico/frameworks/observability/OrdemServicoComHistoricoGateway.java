package br.com.fiap.oficina.ordemservico.frameworks.observability;

import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.TransicaoStatusOrdemServico;
import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.HistoricoStatusOrdemServicoJpa;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/** Infrastructure decorator: keeps the save and history read in the same transaction. */
public class OrdemServicoComHistoricoGateway implements OrdemServicoGateway {
    private final OrdemServicoGateway delegate;
    private final HistoricoStatusOrdemServicoJpa historico;
    private final Consumer<TransicaoStatusOrdemServico> publisher;
    private final TransactionTemplate transactions;

    public OrdemServicoComHistoricoGateway(OrdemServicoGateway delegate, HistoricoStatusOrdemServicoJpa historico,
                                          Consumer<TransicaoStatusOrdemServico> publisher, TransactionTemplate transactions) {
        this.delegate = delegate;
        this.historico = historico;
        this.publisher = publisher;
        this.transactions = transactions;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        return transactions.execute(tx -> {
            long cursor = historico.bloquearEObterUltimoRegistro(ordemServico.id().value());
            var saved = delegate.salvar(ordemServico);
            var transicoes = historico.buscarApos(ordemServico.id().value(), cursor);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (var transicao : transicoes) {
                        try {
                            publisher.accept(transicao);
                        } catch (RuntimeException exception) {
                            // A telemetry failure must not turn an already committed save into an API failure.
                            LoggerFactory.getLogger(OrdemServicoComHistoricoGateway.class).warn(
                                    "Falha ao publicar observabilidade da transicao de OS");
                        }
                    }
                }
            });
            return saved;
        });
    }

    @Override
    public Optional<OrdemServico> buscarPorId(OrdemServicoId id) { return delegate.buscarPorId(id); }
    @Override
    public List<OrdemServico> buscarPorClienteOrdenado(UUID id) { return delegate.buscarPorClienteOrdenado(id); }
    @Override
    public List<OrdemServico> buscarPorStatusOrdenado(Integer status) { return delegate.buscarPorStatusOrdenado(status); }
    @Override
    public List<OrdemServico> buscarTodosOrdenado() { return delegate.buscarTodosOrdenado(); }
}
