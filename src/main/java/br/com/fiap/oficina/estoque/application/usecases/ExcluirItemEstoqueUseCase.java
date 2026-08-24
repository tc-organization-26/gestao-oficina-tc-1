package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;

public interface ExcluirItemEstoqueUseCase {
    void excluir(ItemEstoqueId itemEstoqueId);
}