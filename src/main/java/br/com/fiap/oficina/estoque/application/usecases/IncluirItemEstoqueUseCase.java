package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.application.dtos.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;

public interface IncluirItemEstoqueUseCase {
    ItemEstoque incluir(IncluirItemEstoqueCommand command);
}