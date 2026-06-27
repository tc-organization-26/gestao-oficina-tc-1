package br.com.fiap.oficina.servico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.shared.domain.Entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class Servico extends Entity<ServicoId> {

    private final ServicoId id;
    private final String codigo;
    private String descricao;
    private BigDecimal valorUnitario;
    private Integer tempoEstimadoMinutos;
    private boolean ativo;
    private final OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public Servico(
            ServicoId id,
            String codigo,
            String descricao,
            BigDecimal valorUnitario,
            Integer tempoEstimadoMinutos,
            boolean ativo,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm) {
        validarCodigo(codigo);
        validarDados(descricao, valorUnitario, tempoEstimadoMinutos);

        if (id == null) {
            throw new DomainException("Id do serviço é obrigatório.");
        }

        this.id = id;
        this.codigo = codigo.trim().toUpperCase();
        this.descricao = descricao.trim();
        this.valorUnitario = valorUnitario;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Servico criar(
            String codigo,
            String descricao,
            BigDecimal valorUnitario,
            Integer tempoEstimadoMinutos) {
        return new Servico(
                ServicoId.novo(),
                codigo,
                descricao,
                valorUnitario,
                tempoEstimadoMinutos,
                true,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void atualizar(String descricao, BigDecimal valorUnitario, Integer tempoEstimadoMinutos) {
        validarDados(descricao, valorUnitario, tempoEstimadoMinutos);

        this.descricao = descricao.trim();
        this.valorUnitario = valorUnitario;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new DomainException("Código do serviço é obrigatório.");
        }

        if (codigo.length() > 30) {
            throw new DomainException("Código do serviço deve ter até 30 caracteres.");
        }
    }

    private static void validarDados(String descricao, BigDecimal valorUnitario, Integer tempoEstimadoMinutos) {
        if (descricao == null || descricao.isBlank()) {
            throw new DomainException("Descrição do serviço é obrigatória.");
        }

        if (descricao.length() > 255) {
            throw new DomainException("Descrição do serviço deve ter até 255 caracteres.");
        }

        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Valor unitário do serviço deve ser maior ou igual a zero.");
        }

        if (tempoEstimadoMinutos == null || tempoEstimadoMinutos <= 0) {
            throw new DomainException("Tempo estimado do serviço deve ser maior que zero.");
        }
    }

    @Override
    public ServicoId id() {
        return id;
    }

    public String codigo() {
        return codigo;
    }

    public String descricao() {
        return descricao;
    }

    public BigDecimal valorUnitario() {
        return valorUnitario;
    }

    public Integer tempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public boolean ativo() {
        return ativo;
    }

    public OffsetDateTime criadoEm() {
        return criadoEm;
    }

    public OffsetDateTime atualizadoEm() {
        return atualizadoEm;
    }
}
