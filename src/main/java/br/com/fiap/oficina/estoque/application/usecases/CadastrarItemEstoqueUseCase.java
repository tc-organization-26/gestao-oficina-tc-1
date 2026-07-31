package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.application.dtos.CadastrarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;

public interface CadastrarItemEstoqueUseCase {
    ItemEstoque cadastrar(CadastrarItemEstoqueCommand command);
}