package br.com.fiap.oficina.ordemservico.domain.event;

import java.util.UUID;

public record FaltaPecaEstoqueEvent(UUID ordemServicoId, UUID itemEstoqueId, double quantidadeSolicitada) {
}