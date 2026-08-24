package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.entities.OrcamentoItemServico;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;

import java.util.UUID;
import java.util.stream.Collectors;

public final class OrcamentoMapper {

    public static Orcamento toDomain(OrcamentoJpaEntity entity) {
        var orcamento = new Orcamento(
                entity.getId(),
                new br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId(entity.getOrdemServicoId()),
                entity.getStatus(),
                entity.getDataFechamento()
        );
        var itens = entity.getItens().stream()
                .map(item -> new OrcamentoItemServico(new ServicoId(item.getServicoId()), item.getQuantidade()))
                .collect(Collectors.toList());
        itens.forEach(orcamento::adicionarItemServico);

        var itensPeca = entity.getItensPeca().stream()
                .map(item -> new ItemPeca(new ItemEstoqueId(item.getItemEstoqueId()), item.getQuantidade()))
                .collect(Collectors.toList());
        itensPeca.forEach(orcamento::adicionarItemPeca);

        return orcamento;
    }

    public static OrcamentoJpaEntity toJpaEntity(Orcamento orcamento) {
        var entity = new OrcamentoJpaEntity(
                orcamento.id().value(),
                orcamento.ordemServicoId().value(),
                orcamento.status(),
                orcamento.dataFechamento()
        );
        var itens = orcamento.itens().stream()
                .map(it -> new OrcamentoItemServicoJpaEntity(UUID.randomUUID(), orcamento.id().value(), it.servicoId().value(), it.quantidade()))
                .collect(Collectors.toList());
        entity.setItens(itens);

        var itensPeca = orcamento.itensPeca().stream()
                .map(it -> new OrcamentoItemPecaJpaEntity(UUID.randomUUID(), orcamento.id().value(), it.itemEstoqueId().value(), it.quantidade()))
                .collect(Collectors.toList());
        entity.setItensPeca(itensPeca);

        return entity;
    }
}

