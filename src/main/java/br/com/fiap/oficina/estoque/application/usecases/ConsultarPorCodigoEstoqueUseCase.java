package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;

public interface ConsultarPorCodigoEstoqueUseCase {
    ItemEstoque consultarPorCodigo(String codigo);
}
