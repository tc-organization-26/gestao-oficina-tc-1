package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "orcamento_item_peca")
public class OrcamentoItemPecaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "orcamento_id", insertable = false, updatable = false)
    private UUID orcamentoId;

    @Column(name = "item_estoque_id")
    private UUID itemEstoqueId;

    @Column(name = "quantidade")
    private double quantidade;

    public OrcamentoItemPecaJpaEntity() {}

    public OrcamentoItemPecaJpaEntity(UUID id, UUID orcamentoId, UUID itemEstoqueId, double quantidade) {
        this.id = id;
        this.orcamentoId = orcamentoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public UUID getId() { return id; }
    public UUID getOrcamentoId() { return orcamentoId; }
    public UUID getItemEstoqueId() { return itemEstoqueId; }
    public double getQuantidade() { return quantidade; }
}