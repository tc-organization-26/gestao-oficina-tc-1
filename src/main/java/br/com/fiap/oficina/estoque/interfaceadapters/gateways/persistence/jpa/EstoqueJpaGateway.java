package br.com.fiap.oficina.estoque.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.estoque.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;

import java.util.List;
import java.util.Optional;

public class EstoqueJpaGateway implements EstoqueGateway {

    private final SpringDataEstoqueRepository repository;

    public EstoqueJpaGateway(SpringDataEstoqueRepository repository) {
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
    public Optional<ItemEstoque> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(ItemEstoqueMapper::toDomain);
    }

    @Override
    public List<ItemEstoque> buscarTodos() {
        return repository.findAll().stream()
                .map(ItemEstoqueMapper::toDomain)
                .toList();
    }

    @Override
    public List<ItemEstoque> buscarTodosAtivos() {
        return repository.findAllByAtivoTrue().stream()
                .map(ItemEstoqueMapper::toDomain)
                .toList();
    }
}
