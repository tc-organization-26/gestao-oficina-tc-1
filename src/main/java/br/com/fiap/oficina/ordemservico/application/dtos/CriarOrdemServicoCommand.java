package br.com.fiap.oficina.ordemservico.application.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarOrdemServicoCommand(
        UUID clienteId,
        UUID veiculoId,
        List<ItemServicoCommand> servicos,
        List<ItemPecaCommand> pecas,
        String anotacoes
) {
    public CriarOrdemServicoCommand {
        servicos = servicos == null ? List.of() : List.copyOf(servicos);
        pecas = pecas == null ? List.of() : List.copyOf(pecas);
    }

    public record ItemServicoCommand(
            String codigo,
            BigDecimal quantidade
    ) {
    }

    public record ItemPecaCommand(
            String codigo,
            BigDecimal quantidade
    ) {
    }
}
