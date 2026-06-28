package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class Diagnostico {

	private final UUID id;
	private String descricao;
	private final OffsetDateTime criadoEm;
	private OffsetDateTime atualizadoEm;

	public Diagnostico(UUID id, String descricao, OffsetDateTime criadoEm, OffsetDateTime atualizadoEm) {
		if (id == null) {
			throw new DomainException("Id do diagnostico e obrigatorio.");
		}
		validarDescricao(descricao);
		this.id = id;
		this.descricao = descricao;
		this.criadoEm = criadoEm == null ? OffsetDateTime.now(ZoneOffset.UTC) : criadoEm;
		this.atualizadoEm = atualizadoEm == null ? this.criadoEm : atualizadoEm;
	}

	public static Diagnostico registrar(String descricao) {
		var agora = OffsetDateTime.now(ZoneOffset.UTC);
		return new Diagnostico(UUID.randomUUID(), descricao, agora, agora);
	}

	public void atualizarDescricao(String descricao) {
		validarDescricao(descricao);
		this.descricao = descricao;
		this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
	}

	private static void validarDescricao(String descricao) {
		if (descricao == null || descricao.isBlank()) {
			throw new DomainException("Descricao do diagnostico e obrigatoria.");
		}
	}

	public UUID id() { return id; }
	public String descricao() { return descricao; }
	public OffsetDateTime criadoEm() { return criadoEm; }
	public OffsetDateTime atualizadoEm() { return atualizadoEm; }
}
