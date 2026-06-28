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
        var entity = ItemEstoqueMapper.toEntity(itemEstoque);
        return ItemEstoqueMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<ItemEstoque> buscarPorId(ItemEstoqueId itemEstoqueId) {
        return repository.findById(itemEstoqueId.value()).map(ItemEstoqueMapper::toDomain);
    }

    @Override
    public List<ItemEstoque> buscarTodos() {
        return repository.findAll().stream()
                .map(ItemEstoqueMapper::toDomain)
                .toList();
    }
}