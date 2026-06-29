package br.com.fiap.oficina.estoque.application.port.out;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepositoryPort {
    boolean existePorCodigo(String codigo);
    ItemEstoque salvar(ItemEstoque itemEstoque);
    Optional<ItemEstoque> buscarPorId(ItemEstoqueId itemEstoqueId);
    Optional<ItemEstoque> buscarPorCodigo(String codigo);
    List<ItemEstoque> buscarTodos();
    List<ItemEstoque> buscarTodosAtivos();
}