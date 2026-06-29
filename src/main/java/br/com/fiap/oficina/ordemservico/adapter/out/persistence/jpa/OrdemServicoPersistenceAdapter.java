package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrdemServicoPersistenceAdapter implements OrdemServicoRepositoryPort {

    private final SpringDataOrdemServicoRepository repository;
    private final OrdemServicoJpaMapper mapper = new OrdemServicoJpaMapper();

    public OrdemServicoPersistenceAdapter(SpringDataOrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        var persisted = repository.saveAndFlush(mapper.toEntity(ordemServico));
        return repository.findById(persisted.getId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada apos salvar."));
    }

    @Override
    public Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId) {
        return repository.findById(ordemServicoId.value()).map(mapper::toDomain);
    }

    @Override
    public List<OrdemServico> buscarPorClienteOrdenado(UUID clienteId) {
        return repository.findAllByClienteIdOrderByDataRecebimentoAsc(clienteId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarPorStatusOrdenado(Integer statusOrdemServico) {
        return repository.findAllByStatusOrdemServicoOrderByDataRecebimentoAsc(statusOrdemServico)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarTodosOrdenado() {
        return repository.findAllByOrderByDataRecebimentoAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}