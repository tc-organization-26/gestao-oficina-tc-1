package br.com.fiap.oficina.estoque.application.port.in;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import java.util.List;

public interface ConsultarEstoqueAtivoUseCase {
    List<ItemEstoque> consultarAtivos();
}
