package br.com.fiap.oficina.ordemservico.domain.model;

import java.util.UUID;

public record OrcamentoId(UUID value) {
    public static OrcamentoId from(UUID id) { return new OrcamentoId(id); }
}
