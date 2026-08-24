package br.com.fiap.oficina.ordemservico.domain.valueobjects;

import java.util.UUID;

public record OrcamentoId(UUID value) {
    public static OrcamentoId from(UUID id) { return new OrcamentoId(id); }
}
