package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;

import java.util.UUID;

public interface EntregarOrdemServicoUseCase {
    OrdemServico entregar(UUID ordemId);
}
