package br.com.fiap.oficina.estoque.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.estoque.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;

public class ItemEstoqueMapper {
    public static ItemEstoque toDomain(ItemEstoqueJpaEntity entity) {
        return new ItemEstoque(
                new ItemEstoqueId(entity.getId()),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getValorUnitario(),
                entity.getQuantidadeDisponivel(),
                entity.isAtivo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }

    public static ItemEstoqueJpaEntity toEntity(ItemEstoque itemEstoque) {
        return new ItemEstoqueJpaEntity(
                itemEstoque.id().value(),
                itemEstoque.codigo(),
                itemEstoque.descricao(),
                itemEstoque.valorUnitario(),
                itemEstoque.quantidadeDisponivel(),
                itemEstoque.ativo(),
                itemEstoque.criadoEm(),
                itemEstoque.atualizadoEm());
    }
}
