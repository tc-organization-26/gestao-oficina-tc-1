package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrdemServicoPersistenceAdapter implements OrdemServicoRepositoryPort {

    private final SpringDataOrdemServicoRepository repository;
    private final OrcamentoSpringDataRepository orcamentoRepository;
    private final OrdemServicoJpaMapper mapper = new OrdemServicoJpaMapper();

    public OrdemServicoPersistenceAdapter(
            SpringDataOrdemServicoRepository repository,
            OrcamentoSpringDataRepository orcamentoRepository) {
        this.repository = repository;
        this.orcamentoRepository = orcamentoRepository;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        var persisted = repository.saveAndFlush(mapper.toEntity(ordemServico));
        return repository.findById(persisted.getId())
                .map(this::toDomainComOrcamento)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada apos salvar."));
    }

    @Override
    public Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId) {
        return repository.findById(ordemServicoId.value()).map(this::toDomainComOrcamento);
    }

    @Override
    public List<OrdemServico> buscarPorClienteOrdenado(UUID clienteId) {
        return repository.findAllByClienteIdOrderByDataRecebimentoAsc(clienteId)
                .stream()
                .map(this::toDomainComOrcamento)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarPorStatusOrdenado(Integer statusOrdemServico) {
        return repository.findAllByStatusOrdemServicoOrderByDataRecebimentoAsc(statusOrdemServico)
                .stream()
                .map(this::toDomainComOrcamento)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarTodosOrdenado() {
        return repository.findAllByOrderByDataRecebimentoAsc()
                .stream()
                .map(this::toDomainComOrcamento)
                .toList();
    }

    private OrdemServico toDomainComOrcamento(OrdemServicoJpaEntity entity) {
        var orcamento = orcamentoRepository.findByOrdemServicoId(entity.getId())
                .map(OrcamentoMapper::toDomain)
                .orElse(null);
        return mapper.toDomain(entity, orcamento);
    }
}
