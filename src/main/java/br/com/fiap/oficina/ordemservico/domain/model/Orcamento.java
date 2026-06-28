package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Orcamento {

	private final UUID id;
	private final OrcamentoId orcamentoId;
	private final OrdemServicoId ordemServicoId;
	private final List<OrcamentoItemServico> itens;
	private StatusOrcamento status;
	private OffsetDateTime dataFechamento;

	public Orcamento(UUID id, OrdemServicoId ordemServicoId) {
		if (ordemServicoId == null) throw new DomainException("OrdemServicoId obrigatorio");
		this.id = id;
		this.orcamentoId = OrcamentoId.from(id);
		this.ordemServicoId = ordemServicoId;
		this.itens = new ArrayList<>();
		this.status = StatusOrcamento.ABERTO;
		this.dataFechamento = null;
	}

	public Orcamento(UUID id, OrdemServicoId ordemServicoId, StatusOrcamento status, OffsetDateTime dataFechamento) {
		if (ordemServicoId == null) throw new DomainException("OrdemServicoId obrigatorio");
		this.id = id;
		this.orcamentoId = OrcamentoId.from(id);
		this.ordemServicoId = ordemServicoId;
		this.itens = new ArrayList<>();
		this.status = status == null ? StatusOrcamento.ABERTO : status;
		this.dataFechamento = dataFechamento;
	}

	public static Orcamento novo(OrdemServicoId ordemServicoId) {
		return new Orcamento(UUID.randomUUID(), ordemServicoId);
	}

	public void adicionarItemServico(OrcamentoItemServico item) {
		if (item == null) throw new DomainException("Item obrigatorio");
		if (this.status != StatusOrcamento.ABERTO) {
			throw new DomainException("Nao e possivel adicionar itens a um orcamento fechado");
		}
		this.itens.add(item);
	}

	public void fechar() {
		if (this.status == StatusOrcamento.FINALIZADO) {
			throw new DomainException("Orcamento ja foi finalizado");
		}
		if (this.itens.isEmpty()) {
			throw new DomainException("Orcamento deve ter pelo menos um item para ser finalizado");
		}
		this.status = StatusOrcamento.FINALIZADO;
		this.dataFechamento = OffsetDateTime.now(ZoneOffset.UTC);
	}

	public List<OrcamentoItemServico> itens() { return List.copyOf(itens); }

	public OrcamentoId id() { return orcamentoId; }
	public OrdemServicoId ordemServicoId() { return ordemServicoId; }
	public StatusOrcamento status() { return status; }
	public OffsetDateTime dataFechamento() { return dataFechamento; }
}
