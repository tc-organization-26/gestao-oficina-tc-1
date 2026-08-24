package br.com.fiap.oficina.estoque.frameworks.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_estoque")
public class ItemEstoqueJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(name = "valor_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "quantidade_disponivel", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeDisponivel;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected ItemEstoqueJpaEntity() {
    }

    public ItemEstoqueJpaEntity(
            UUID id,
            String codigo,
            String descricao,
            BigDecimal valorUnitario,
            BigDecimal quantidadeDisponivel,
            boolean ativo,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public BigDecimal getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public boolean isAtivo() { return ativo; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}