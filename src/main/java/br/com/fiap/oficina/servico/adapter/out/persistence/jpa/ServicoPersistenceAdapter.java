package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;

public class ServicoPersistenceAdapter implements ServicoRepositoryPort {

    private final SpringDataServicoRepository repository;

    public ServicoPersistenceAdapter(SpringDataServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return repository.existsByCodigo(codigo);
    }

    @Override
    public Servico salvar(Servico servico) {
        var entity = new ServicoJpaEntity(
                servico.id().value(),
                servico.codigo(),
                servico.descricao(),
                servico.valorUnitario(),
                servico.tempoEstimadoMinutos(),
                servico.ativo(),
                servico.criadoEm(),
                servico.atualizadoEm());

        var entitySalva = repository.save(entity);

        return toDomain(entitySalva);
    }

    @Override
    public Optional<Servico> buscarPorId(ServicoId servicoId) {
        return repository.findById(servicoId.value())
                .map(this::toDomain);
    }

    @Override
    public List<Servico> buscarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(ServicoId servicoId) {
        repository.deleteById(servicoId.value());
    }

    private Servico toDomain(ServicoJpaEntity entity) {
        return new Servico(
                new ServicoId(entity.getId()),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getValorUnitario(),
                entity.getTempoEstimadoMinutos(),
                entity.isAtivo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }
}