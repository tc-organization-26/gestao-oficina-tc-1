package br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "diagnostico")
public class DiagnosticoJpaEntity {

    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoJpaEntity ordemServico;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected DiagnosticoJpaEntity() {
    }

    public DiagnosticoJpaEntity(
            UUID id,
            OrdemServicoJpaEntity ordemServico,
            String descricao,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm) {
        this.id = id;
        this.ordemServico = ordemServico;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public OrdemServicoJpaEntity getOrdemServico() { return ordemServico; }
    public String getDescricao() { return descricao; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
