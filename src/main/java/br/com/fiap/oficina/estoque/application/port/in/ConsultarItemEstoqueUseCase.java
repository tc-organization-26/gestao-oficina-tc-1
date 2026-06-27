package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;

public interface ConsultarItemEstoqueUseCase {
    ItemEstoque consultarPorId(ItemEstoqueId itemEstoqueId);
}