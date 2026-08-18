package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarOrdemServicoRequest(
	@NotNull UUID clienteId,
	@NotNull UUID veiculoId,
	@NotNull List<@Valid ItemServicoRequest> servicos,
	@NotNull List<@Valid ItemPecaRequest> pecas,
	String anotacoes) {

	public record ItemServicoRequest(
			@NotBlank @Size(max = 30) String codigo,
			@NotNull @DecimalMin(value = "0.001") BigDecimal quantidade
	) {
	}

	public record ItemPecaRequest(
			@NotBlank @Size(max = 30) String codigo,
			@NotNull @DecimalMin(value = "0.001") BigDecimal quantidade
	) {
	}
}
