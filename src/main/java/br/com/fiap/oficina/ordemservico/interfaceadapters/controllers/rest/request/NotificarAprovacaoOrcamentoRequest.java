package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificarAprovacaoOrcamentoRequest(
        @NotNull DecisaoOrcamento decisao,
        @Size(max = 80) String origem,
        @Size(max = 120) String protocoloExterno
) {
    public enum DecisaoOrcamento {
        APROVADO,
        RECUSADO
    }
}
