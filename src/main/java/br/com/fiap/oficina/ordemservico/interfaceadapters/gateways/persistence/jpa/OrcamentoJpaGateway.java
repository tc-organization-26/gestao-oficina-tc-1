package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;

import java.util.Optional;

public class OrcamentoJpaGateway implements OrcamentoGateway {

    private final OrcamentoSpringDataRepository repository;

    public OrcamentoJpaGateway(OrcamentoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        var orcamentoEntity = OrcamentoMapper.toJpaEntity(orcamento);
        var entitySalva = repository.save(orcamentoEntity);
        return OrcamentoMapper.toDomain(entitySalva);
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
