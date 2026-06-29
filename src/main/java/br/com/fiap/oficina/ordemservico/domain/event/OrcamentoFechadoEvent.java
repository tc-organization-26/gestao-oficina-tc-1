package br.com.fiap.oficina.ordemservico.domain.event;

import java.util.UUID;

public record OrcamentoFechadoEvent(UUID ordemServicoId, UUID clienteId) {}
