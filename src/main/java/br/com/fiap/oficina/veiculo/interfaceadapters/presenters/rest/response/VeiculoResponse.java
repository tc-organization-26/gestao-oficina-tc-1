package br.com.fiap.oficina.veiculo.interfaceadapters.presenters.rest.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;

public record VeiculoResponse(
        UUID id,
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static VeiculoResponse from(Veiculo veiculo) {
        return new VeiculoResponse(
                veiculo.id().value(),
                veiculo.clienteId().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano(),
                veiculo.criadoEm(),
                veiculo.atualizadoEm());
    }
}
