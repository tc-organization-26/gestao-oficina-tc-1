package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.application.command.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;

public interface AtualizarItemEstoqueUseCase {
    ItemEstoque atualizar(AtualizarItemEstoqueCommand command);
}