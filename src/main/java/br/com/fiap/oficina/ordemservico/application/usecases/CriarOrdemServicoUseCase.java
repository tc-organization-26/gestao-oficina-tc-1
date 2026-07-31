package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface CriarOrdemServicoUseCase {
    OrdemServico criar(CriarOrdemServicoCommand command);
}