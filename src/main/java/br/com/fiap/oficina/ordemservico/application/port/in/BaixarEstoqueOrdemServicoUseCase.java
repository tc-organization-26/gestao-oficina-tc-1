package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.BaixarEstoqueOrdemCommand;

public interface BaixarEstoqueOrdemServicoUseCase {
    void baixarEstoque(BaixarEstoqueOrdemCommand command);
}
