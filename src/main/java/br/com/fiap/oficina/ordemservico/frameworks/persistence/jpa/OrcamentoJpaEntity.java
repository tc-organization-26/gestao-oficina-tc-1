package br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa;

import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusOrcamento status;

    @Column(name = "data_fechamento")
    private OffsetDateTime dataFechamento;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private List<OrcamentoItemServicoJpaEntity> itens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private List<OrcamentoItemPecaJpaEntity> itensPeca = new ArrayList<>();

    public OrcamentoJpaEntity() {}

    public OrcamentoJpaEntity(UUID id, UUID ordemServicoId) {
        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.status = StatusOrcamento.ABERTO;
        this.dataFechamento = null;
    }

    public OrcamentoJpaEntity(UUID id, UUID ordemServicoId, StatusOrcamento status, OffsetDateTime dataFechamento) {
        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.status = status == null ? StatusOrcamento.ABERTO : status;
        this.dataFechamento = dataFechamento;
    }

    public UUID getId() { return id; }
    public UUID getOrdemServicoId() { return ordemServicoId; }
    public StatusOrcamento getStatus() { return status; }
    public void setStatus(StatusOrcamento status) { this.status = status; }
    public OffsetDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(OffsetDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
    public List<OrcamentoItemServicoJpaEntity> getItens() { return itens; }
    public void setItens(List<OrcamentoItemServicoJpaEntity> itens) { this.itens = itens; }
    public List<OrcamentoItemPecaJpaEntity> getItensPeca() { return itensPeca; }
    public void setItensPeca(List<OrcamentoItemPecaJpaEntity> itensPeca) { this.itensPeca = itensPeca; }
}