package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoItemServico;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.servico.domain.model.ServicoId;

import java.util.UUID;
import java.util.stream.Collectors;

public final class OrcamentoMapper {

    public static Orcamento toDomain(OrcamentoJpaEntity e) {
        var orcamento = new Orcamento(
                e.getId(),
                new br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId(e.getOrdemServicoId()),
                e.getStatus(),
                e.getDataFechamento()
        );
        var itens = e.getItens().stream()
                .map(i -> new OrcamentoItemServico(new ServicoId(i.getServicoId()), i.getQuantidade()))
                .collect(Collectors.toList());
        itens.forEach(orcamento::adicionarItemServico);

        var itensPeca = e.getItensPeca().stream()
                .map(i -> new ItemPeca(new ItemEstoqueId(i.getItemEstoqueId()), i.getQuantidade()))
                .collect(Collectors.toList());
        itensPeca.forEach(orcamento::adicionarItemPeca);

        return orcamento;
    }

    public static OrcamentoJpaEntity toJpa(Orcamento orcamento) {
        var e = new OrcamentoJpaEntity(
                orcamento.id().value(),
                orcamento.ordemServicoId().value(),
                orcamento.status(),
                orcamento.dataFechamento()
        );
        var itens = orcamento.itens().stream()
                .map(it -> new OrcamentoItemServicoJpaEntity(UUID.randomUUID(), orcamento.id().value(), it.servicoId().value(), it.quantidade()))
                .collect(Collectors.toList());
        e.setItens(itens);

        var itensPeca = orcamento.itensPeca().stream()
                .map(it -> new OrcamentoItemPecaJpaEntity(UUID.randomUUID(), orcamento.id().value(), it.itemEstoqueId().value(), it.quantidade()))
                .collect(Collectors.toList());
        e.setItensPeca(itensPeca);

        return e;
    }
}

