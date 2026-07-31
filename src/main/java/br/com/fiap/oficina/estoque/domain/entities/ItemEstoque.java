package br.com.fiap.oficina.estoque.domain.entities;

import br.com.fiap.oficina.estoque.domain.enums.*;

import br.com.fiap.oficina.estoque.domain.valueobjects.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.shared.domain.entities.Entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class ItemEstoque extends Entity<ItemEstoqueId> {

    private final ItemEstoqueId id;
    private final String codigo;
    private String descricao;
    private BigDecimal valorUnitario;
    private BigDecimal quantidadeDisponivel;
    private boolean ativo;
    private final OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public ItemEstoque(
            ItemEstoqueId id,
            String codigo,
            String descricao,
            BigDecimal valorUnitario,
            BigDecimal quantidadeDisponivel,
            boolean ativo,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm) {
        validarDados(codigo, descricao, valorUnitario, quantidadeDisponivel);
        if (id == null) {
            throw new DomainException("Id do item de estoque e obrigatorio.");
        }
        this.id = id;
        this.codigo = codigo.trim().toUpperCase();
        this.descricao = descricao.trim();
        this.valorUnitario = valorUnitario;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static ItemEstoque criar(String codigo, String descricao, BigDecimal valorUnitario, BigDecimal quantidadeInicial) {
        var agora = OffsetDateTime.now(ZoneOffset.UTC);
        return new ItemEstoque(ItemEstoqueId.novo(), codigo, descricao, valorUnitario, quantidadeInicial, true, agora, agora);
    }

    public void atualizar(String descricao, BigDecimal valorUnitario) {
        validarDados(this.codigo, descricao, valorUnitario, this.quantidadeDisponivel);
        this.descricao = descricao.trim();
        this.valorUnitario = valorUnitario;
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void incluir(BigDecimal quantidade) {
        validarQuantidadePositiva(quantidade, "Quantidade de inclusao deve ser maior que zero.");
        this.quantidadeDisponivel = this.quantidadeDisponivel.add(quantidade);
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void baixar(BigDecimal quantidade) {
        validarQuantidadePositiva(quantidade, "Quantidade de baixa deve ser maior que zero.");
        if (this.quantidadeDisponivel.compareTo(quantidade) < 0) {
            throw new DomainException("Quantidade indisponivel em estoque.");
        }
        this.quantidadeDisponivel = this.quantidadeDisponivel.subtract(quantidade);
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void desativar() {
        this.ativo = false;
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static void validarDados(String codigo, String descricao, BigDecimal valorUnitario, BigDecimal quantidadeDisponivel) {
        if (codigo == null || codigo.isBlank()) {
            throw new DomainException("Codigo do item de estoque e obrigatorio.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new DomainException("Descricao do item de estoque e obrigatoria.");
        }
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Valor unitario deve ser maior ou igual a zero.");
        }
        if (quantidadeDisponivel == null || quantidadeDisponivel.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Quantidade disponivel deve ser maior ou igual a zero.");
        }
    }

    private static void validarQuantidadePositiva(BigDecimal quantidade, String mensagem) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(mensagem);
        }
    }

    @Override public ItemEstoqueId id() { return id; }
    public String codigo() { return codigo; }
    public String descricao() { return descricao; }
    public BigDecimal valorUnitario() { return valorUnitario; }
    public BigDecimal quantidadeDisponivel() { return quantidadeDisponivel; }
    public boolean ativo() { return ativo; }
    public OffsetDateTime criadoEm() { return criadoEm; }
    public OffsetDateTime atualizadoEm() { return atualizadoEm; }
}