package br.com.fiap.oficina.veiculo.application.command;

import java.util.UUID;

public record ConsultarVeiculosPorClienteCommand(
        UUID clienteId
) {
}
