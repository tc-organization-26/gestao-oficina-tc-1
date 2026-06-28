package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orcamento")
public class OrcamentoJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "ordem_servico_id")
    private UUID ordemServicoId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orcamento_id")
    private List<OrcamentoItemServicoJpaEntity> itens = new ArrayList<>();

    public OrcamentoJpaEntity() {}

    public OrcamentoJpaEntity(UUID id, UUID ordemServicoId) {
        this.id = id;
        this.ordemServicoId = ordemServicoId;
    }

    public UUID getId() { return id; }
    public UUID getOrdemServicoId() { return ordemServicoId; }
    public List<OrcamentoItemServicoJpaEntity> getItens() { return itens; }
    public void setItens(List<OrcamentoItemServicoJpaEntity> itens) { this.itens = itens; }
}
