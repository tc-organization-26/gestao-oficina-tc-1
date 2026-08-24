package br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orcamento_item_servico")
public class OrcamentoItemServicoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "orcamento_id", insertable = false, updatable = false)
    private UUID orcamentoId;

    @Column(name = "servico_id")
    private UUID servicoId;

    @Column(name = "quantidade")
    private BigDecimal quantidade;

    public OrcamentoItemServicoJpaEntity() {}

    public OrcamentoItemServicoJpaEntity(UUID id, UUID orcamentoId, UUID servicoId, BigDecimal quantidade) {
        this.id = id;
        this.orcamentoId = orcamentoId;
        this.servicoId = servicoId;
        this.quantidade = quantidade;
    }

    public UUID getId() { return id; }
    public UUID getOrcamentoId() { return orcamentoId; }
    public UUID getServicoId() { return servicoId; }
    public BigDecimal getQuantidade() { return quantidade; }
}
