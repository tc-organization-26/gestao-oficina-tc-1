package br.com.fiap.oficina.estoque.adapter.out.persistence.jpa;

import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;

import java.util.List;
import java.util.Optional;

public class EstoquePersistenceAdapter implements EstoqueRepositoryPort {

    private final SpringDataEstoqueRepository repository;

    public EstoquePersistenceAdapter(SpringDataEstoqueRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return repository.existsByCodigo(codigo);
    }

    @Override
    public ItemEstoque salvar(ItemEstoque itemEstoque) {
        var entity = new ItemEstoqueJpaEntity(
                itemEstoque.id().value(),
                itemEstoque.codigo(),
                itemEstoque.descricao(),
                itemEstoque.valorUnitario(),
                itemEstoque.quantidadeDisponivel(),
                itemEstoque.ativo(),
                itemEstoque.criadoEm(),
                itemEstoque.atualizadoEm());
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<ItemEstoque> buscarPorId(ItemEstoqueId itemEstoqueId) {
        return repository.findById(itemEstoqueId.value()).map(this::toDomain);
    }

    @Override
    public List<ItemEstoque> buscarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private ItemEstoque toDomain(ItemEstoqueJpaEntity entity) {
        return new ItemEstoque(
                new ItemEstoqueId(entity.getId()),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getValorUnitario(),
                entity.getQuantidadeDisponivel(),
                entity.isAtivo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }
}