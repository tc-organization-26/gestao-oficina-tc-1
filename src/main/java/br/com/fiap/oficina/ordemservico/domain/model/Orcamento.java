package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Orcamento {

    private final UUID id;
    private final OrcamentoId orcamentoId;
    private final OrdemServicoId ordemServicoId;
    private final List<OrcamentoItemServico> itens;

    public Orcamento(java.util.UUID id, OrdemServicoId ordemServicoId) {
        if (ordemServicoId == null) throw new DomainException("OrdemServicoId obrigatorio");
        this.id = id;
        this.orcamentoId = OrcamentoId.from(id);
        this.ordemServicoId = ordemServicoId;
        this.itens = new ArrayList<>();
    }

    public static Orcamento novo(OrdemServicoId ordemServicoId) {
        return new Orcamento(UUID.randomUUID(), ordemServicoId);
    }

    public void adicionarItemServico(OrcamentoItemServico item) {
        if (item == null) throw new DomainException("Item obrigatorio");
        this.itens.add(item);
    }

    public List<OrcamentoItemServico> itens() { return List.copyOf(itens); }

    public OrcamentoId id() { return orcamentoId; }
    public OrdemServicoId ordemServicoId() { return ordemServicoId; }
}

