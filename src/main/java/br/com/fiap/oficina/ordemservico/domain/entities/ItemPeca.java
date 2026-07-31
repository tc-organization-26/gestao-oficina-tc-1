package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

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
