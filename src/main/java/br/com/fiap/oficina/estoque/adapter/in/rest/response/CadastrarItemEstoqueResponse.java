package br.com.fiap.oficina.estoque.adapter.in.rest.response;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import java.util.UUID;

public record CadastrarItemEstoqueResponse(
        UUID id,
        String codigo,
        String descricao,
        double valorUnitario
) {
    public static CadastrarItemEstoqueResponse from(ItemEstoque item) {
        return new CadastrarItemEstoqueResponse(
                item.id().value(),
                item.codigo(),
                item.descricao(),
                item.valorUnitario().doubleValue()
        );
    }
}
