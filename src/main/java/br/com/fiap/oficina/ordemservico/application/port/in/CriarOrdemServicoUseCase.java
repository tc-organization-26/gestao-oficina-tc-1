package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;

public interface CriarOrdemServicoUseCase {
    OrdemServico criar(CriarOrdemServicoCommand command);
}