package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.application.command.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;

public interface IncluirItemEstoqueUseCase {
    ItemEstoque incluir(IncluirItemEstoqueCommand command);
}