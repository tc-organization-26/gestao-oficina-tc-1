package br.com.fiap.oficina.ordemservico.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface BaixaEstoquePort {
    void baixar(UUID itemEstoqueId, BigDecimal quantidade);
}
