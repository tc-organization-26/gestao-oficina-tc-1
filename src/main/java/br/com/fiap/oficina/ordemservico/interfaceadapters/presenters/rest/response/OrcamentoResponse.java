package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrcamentoResponse(
        UUID id,
        StatusOrcamento status,
        List<OrcamentoItemServicoResponse> itensServico,
        List<OrcamentoItemPecaResponse> itensPeca,
        OffsetDateTime dataFechamento
) {
    public static OrcamentoResponse from(Orcamento orcamento) {
        var itensServico = orcamento.itensServico().stream()
                .map(item -> new OrcamentoItemServicoResponse(item.servicoId().value(), item.quantidade()))
                .toList();
        var itensPeca = orcamento.itensPeca().stream()
                .map(item -> new OrcamentoItemPecaResponse(item.itemEstoqueId().value(), item.quantidade()))
                .toList();
        return new OrcamentoResponse(
                orcamento.id().value(),
                orcamento.status(),
                itensServico,
                itensPeca,
                orcamento.dataFechamento()
        );
    }
}
