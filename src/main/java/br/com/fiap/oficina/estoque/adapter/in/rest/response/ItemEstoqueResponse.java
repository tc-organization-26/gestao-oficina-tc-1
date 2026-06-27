package br.com.fiap.oficina.estoque.adapter.in.rest.response;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ItemEstoqueResponse(
        UUID id,
        String codigo,
        String descricao,
        BigDecimal valorUnitario,
        BigDecimal quantidadeDisponivel,
        boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static ItemEstoqueResponse from(ItemEstoque item) {
        return new ItemEstoqueResponse(
                item.id().value(),
                item.codigo(),
                item.descricao(),
                item.valorUnitario(),
                item.quantidadeDisponivel(),
                item.ativo(),
                item.criadoEm(),
                item.atualizadoEm());
    }
}