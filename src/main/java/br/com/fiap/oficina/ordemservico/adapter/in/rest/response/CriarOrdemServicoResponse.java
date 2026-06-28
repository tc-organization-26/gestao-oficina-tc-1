package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CriarOrdemServicoResponse(
        UUID id,
        Long numero,
        UUID clienteId,
        UUID veiculoId,
        StatusOrdemServico status,
        String anotacoes,
        OffsetDateTime dataRecebimento
) {
    public static CriarOrdemServicoResponse from(OrdemServico ordemServico) {
        return new CriarOrdemServicoResponse(
                ordemServico.id().value(),
                ordemServico.numero(),
                ordemServico.clienteId().value(),
                ordemServico.veiculoId().value(),
                ordemServico.status(),
                ordemServico.anotacoes(),
                ordemServico.dataRecebimento()
        );
    }
}

