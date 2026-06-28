package br.com.fiap.oficina.estoque.adapter.in.rest.response;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import java.util.UUID;

public record AtualizarItemEstoqueResponse(
        UUID id,
        String codigo,
        String descricao,
        double valorUnitario
) {
    public static AtualizarItemEstoqueResponse from(ItemEstoque item) {
        return new AtualizarItemEstoqueResponse(
                item.id().value(),
                item.codigo(),
                item.descricao(),
                item.valorUnitario().doubleValue()
        );
    }
}
