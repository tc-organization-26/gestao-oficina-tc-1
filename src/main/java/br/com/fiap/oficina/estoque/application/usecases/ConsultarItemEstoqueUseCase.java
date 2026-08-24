package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;

public interface ConsultarItemEstoqueUseCase {
    ItemEstoque consultarPorId(ItemEstoqueId itemEstoqueId);
}