package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.application.dtos.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;

public interface AtualizarItemEstoqueUseCase {
    ItemEstoque atualizar(AtualizarItemEstoqueCommand command);
}