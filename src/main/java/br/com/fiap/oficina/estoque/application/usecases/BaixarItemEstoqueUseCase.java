package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.application.dtos.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;

public interface BaixarItemEstoqueUseCase {
    ItemEstoque baixar(BaixarItemEstoqueCommand command);
}