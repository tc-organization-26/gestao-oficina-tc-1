package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusOrdemServicoRequest(
        @NotNull StatusOrdemServico status
) {
}
