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
	private final List<OrcamentoItemServico> itensServico;
	private final List<ItemPeca> itensPeca;
	private StatusOrcamento status;
	private OffsetDateTime dataFechamento;

	public Orcamento(UUID id, OrdemServicoId ordemServicoId) {
		if (ordemServicoId == null) throw new DomainException("OrdemServicoId obrigatorio");
		this.id = id;
		this.orcamentoId = OrcamentoId.from(id);
		this.ordemServicoId = ordemServicoId;
		this.itensServico = new ArrayList<>();
		this.itensPeca = new ArrayList<>();
		this.status = StatusOrcamento.ABERTO;
		this.dataFechamento = null;
	}

	public Orcamento(UUID id, OrdemServicoId ordemServicoId, StatusOrcamento status, OffsetDateTime dataFechamento) {
		if (ordemServicoId == null) throw new DomainException("OrdemServicoId obrigatorio");
		this.id = id;
		this.orcamentoId = OrcamentoId.from(id);
		this.ordemServicoId = ordemServicoId;
		this.itensServico = new ArrayList<>();
		this.itensPeca = new ArrayList<>();
		this.status = status == null ? StatusOrcamento.ABERTO : status;
		this.dataFechamento = dataFechamento;
	}

	public static Orcamento novo(OrdemServicoId ordemServicoId) {
		return new Orcamento(UUID.randomUUID(), ordemServicoId);
	}

	public void adicionarItemServico(OrcamentoItemServico item) {
		if (item == null) throw new DomainException("Item obrigatorio");
		this.itensServico.add(item);
	}

	public void adicionarItemPeca(ItemPeca peca) {
		if (peca == null) throw new DomainException("Peca obrigatoria");
		this.itensPeca.add(peca);
	}

	public void marcarParaVerificacaoEstoque() {
		if (this.status != StatusOrcamento.ABERTO) {
			throw new DomainException("Orcamento deve estar ABERTO para ser verificado");
		}
		this.status = StatusOrcamento.AGUARDANDO_VERIFICACAO_ESTOQUE;
	}

	public void fechar() {
		if (this.status == StatusOrcamento.FINALIZADO || this.status == StatusOrcamento.APROVADO) {
			throw new DomainException("Orcamento ja foi finalizado");
		}
		this.status = StatusOrcamento.FINALIZADO;
		this.dataFechamento = OffsetDateTime.now(ZoneOffset.UTC);
	}

	public void aprovar() {
		if (this.status != StatusOrcamento.FINALIZADO) {
			throw new DomainException("Orcamento deve estar FINALIZADO para ser aprovado");
		}
		this.status = StatusOrcamento.APROVADO;
	}

	public void recusar() {
		if (this.status != StatusOrcamento.FINALIZADO) {
			throw new DomainException("Orcamento deve estar FINALIZADO para ser recusado");
		}
		this.status = StatusOrcamento.RECUSADO;
	}

	public void reabrir() {
		if (this.status != StatusOrcamento.FINALIZADO && this.status != StatusOrcamento.APROVADO) {
			throw new DomainException("Orcamento nao pode ser reaberto no status atual: " + this.status);
		}
		this.itensServico.clear();
		this.status = StatusOrcamento.ABERTO;
		this.dataFechamento = null;
	}

	public List<OrcamentoItemServico> itensServico() { return List.copyOf(itensServico); }
	public List<ItemPeca> itensPeca() { return List.copyOf(itensPeca); }
	public List<OrcamentoItemServico> itens() { return List.copyOf(itensServico); }

	public OrcamentoId id() { return orcamentoId; }
	public OrdemServicoId ordemServicoId() { return ordemServicoId; }
	public StatusOrcamento status() { return status; }
	public OffsetDateTime dataFechamento() { return dataFechamento; }
}
