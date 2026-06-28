package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrcamento;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrcamentoResponse(
        UUID id,
        StatusOrcamento status,
        List<OrcamentoItemResponse> itens,
        OffsetDateTime dataFechamento
) {
    public static OrcamentoResponse from(Orcamento orcamento) {
        var itens = orcamento.itens().stream()
                .map(item -> new OrcamentoItemResponse(item.servicoId().value(), item.quantidade()))
                .toList();
        return new OrcamentoResponse(
                orcamento.id().value(),
                orcamento.status(),
                itens,
                orcamento.dataFechamento()
        );
    }
}
