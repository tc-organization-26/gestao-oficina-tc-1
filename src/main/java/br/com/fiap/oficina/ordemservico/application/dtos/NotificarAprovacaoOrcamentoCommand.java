package br.com.fiap.oficina.ordemservico.application.dtos;

import java.util.UUID;

public record NotificarAprovacaoOrcamentoCommand(
        UUID ordemId,
        DecisaoOrcamento decisao,
        String origem,
        String protocoloExterno
) {
    public enum DecisaoOrcamento {
        APROVADO,
        RECUSADO
    }
}
