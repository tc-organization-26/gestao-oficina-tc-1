package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;

public interface ConsultarPorCodigoEstoqueUseCase {
    ItemEstoque consultarPorCodigo(String codigo);
}
