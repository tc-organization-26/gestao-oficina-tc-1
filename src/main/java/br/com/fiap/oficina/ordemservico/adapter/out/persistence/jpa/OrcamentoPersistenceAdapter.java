package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;

import java.util.Optional;

public class OrcamentoPersistenceAdapter implements OrcamentoRepositoryPort {

    private final OrcamentoSpringDataRepository repository;

    public OrcamentoPersistenceAdapter(OrcamentoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        var e = OrcamentoMapper.toJpa(orcamento);
        var saved = repository.save(e);
        return OrcamentoMapper.toDomain(saved);
    }

    @Override
    public Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId) {
        return repository.findById(orcamentoId.value()).map(OrcamentoMapper::toDomain);
    }

    @Override
    public Optional<Orcamento> buscarPorOrdemServicoId(OrdemServicoId ordemServicoId) {
        return repository.findByOrdemServicoId(ordemServicoId.value()).map(OrcamentoMapper::toDomain);
    }
}