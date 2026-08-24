package br.com.fiap.oficina.veiculo.application.dtos;

import java.util.UUID;

public record ConsultarVeiculosPorClienteCommand(
        UUID clienteId
) {
}
