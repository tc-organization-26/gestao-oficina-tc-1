package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;

public final class ItemPeca {

    private final ItemEstoqueId itemEstoqueId;
    private final BigDecimal quantidade;

    public ItemPeca(ItemEstoqueId itemEstoqueId, BigDecimal quantidade) {
        if (itemEstoqueId == null) throw new DomainException("ItemEstoqueId obrigatorio");
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Quantidade deve ser maior que zero");
        }
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public ItemEstoqueId itemEstoqueId() { return itemEstoqueId; }
    public BigDecimal quantidade() { return quantidade; }
}
