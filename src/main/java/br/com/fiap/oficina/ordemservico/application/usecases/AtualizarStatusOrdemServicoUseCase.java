package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;

import java.util.UUID;

public interface AtualizarStatusOrdemServicoUseCase {
    OrdemServico atualizarStatus(UUID ordemId, StatusOrdemServico status);
}
