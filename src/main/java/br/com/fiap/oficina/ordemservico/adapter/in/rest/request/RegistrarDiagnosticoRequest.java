package br.com.fiap.oficina.ordemservico.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RegistrarDiagnosticoRequest(
        @NotBlank String descricao
) {
}
