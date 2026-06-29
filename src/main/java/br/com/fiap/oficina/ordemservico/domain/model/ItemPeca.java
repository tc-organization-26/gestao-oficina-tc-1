package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.DomainException;

public final class ItemPeca {

    private final ItemEstoqueId itemEstoqueId;
    private final double quantidade;

    public ItemPeca(ItemEstoqueId itemEstoqueId, double quantidade) {
        if (itemEstoqueId == null) throw new DomainException("ItemEstoqueId obrigatorio");
        if (quantidade <= 0) throw new DomainException("Quantidade deve ser maior que zero");
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public ItemEstoqueId itemEstoqueId() { return itemEstoqueId; }
    public double quantidade() { return quantidade; }
}
