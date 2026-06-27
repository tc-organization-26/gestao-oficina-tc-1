package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;

public interface ExcluirItemEstoqueUseCase {
    void excluir(ItemEstoqueId itemEstoqueId);
}