package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;

import java.util.UUID;

public record StatusOrdemServicoResponse(
        UUID id,
        Long numero,
        StatusOrdemServico status,
        String descricao
) {
    public static StatusOrdemServicoResponse from(OrdemServico ordemServico) {
        return new StatusOrdemServicoResponse(
                ordemServico.id().value(),
                ordemServico.numero(),
                ordemServico.status(),
                ordemServico.status().descricao());
    }
}
