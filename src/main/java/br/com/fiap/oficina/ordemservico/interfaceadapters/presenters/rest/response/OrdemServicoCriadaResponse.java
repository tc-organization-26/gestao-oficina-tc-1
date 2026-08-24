package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

import java.util.UUID;

public record OrdemServicoCriadaResponse(
        UUID id,
        Long numero
) {
    public static OrdemServicoCriadaResponse from(OrdemServico ordemServico) {
        return new OrdemServicoCriadaResponse(ordemServico.id().value(), ordemServico.numero());
    }
}
