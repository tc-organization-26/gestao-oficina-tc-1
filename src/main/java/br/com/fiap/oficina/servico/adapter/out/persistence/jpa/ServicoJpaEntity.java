package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "servico")
public class ServicoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "valor_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "tempo_estimado_minutos", nullable = false)
    private Integer tempoEstimadoMinutos;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected ServicoJpaEntity() {
    }

    public ServicoJpaEntity(
            UUID id,
            String codigo,
            String descricao,
            BigDecimal valorUnitario,
            Integer tempoEstimadoMinutos,
            boolean ativo,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm
    ) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public Integer getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}