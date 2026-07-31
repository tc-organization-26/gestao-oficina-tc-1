package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CriarOrdemServicoRequest(
	@NotNull UUID clienteId,
	@NotNull UUID veiculoId,
	String anotacoes) {
}
