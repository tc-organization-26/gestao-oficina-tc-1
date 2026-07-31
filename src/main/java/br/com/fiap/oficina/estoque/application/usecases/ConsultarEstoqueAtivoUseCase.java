package br.com.fiap.oficina.estoque.application.usecases;

import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import java.util.List;

public interface ConsultarEstoqueAtivoUseCase {
    List<ItemEstoque> consultarAtivos();
}
