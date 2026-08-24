package br.com.fiap.oficina.ordemservico.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionarItemPecaOrcamentoCommand(UUID ordemId, String itemEstoqueCodigo, BigDecimal quantidade) {}
