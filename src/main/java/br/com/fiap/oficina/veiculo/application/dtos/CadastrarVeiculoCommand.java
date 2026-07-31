package br.com.fiap.oficina.veiculo.application.dtos;

import java.util.UUID;

public record CadastrarVeiculoCommand(
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        Integer ano
) {
}