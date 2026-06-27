package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.application.command.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;

public interface BaixarItemEstoqueUseCase {
    ItemEstoque baixar(BaixarItemEstoqueCommand command);
}