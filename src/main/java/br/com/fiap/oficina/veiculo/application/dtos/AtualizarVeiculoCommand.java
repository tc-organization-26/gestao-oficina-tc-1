package br.com.fiap.oficina.veiculo.application.dtos;

import java.util.UUID;

public record AtualizarVeiculoCommand(
                UUID veiculoId,
                String marca,
                String modelo,
                Integer ano) {
}