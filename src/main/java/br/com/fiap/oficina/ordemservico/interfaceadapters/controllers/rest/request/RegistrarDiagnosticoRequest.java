package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RegistrarDiagnosticoRequest(
        @NotBlank String descricao
) {
}
