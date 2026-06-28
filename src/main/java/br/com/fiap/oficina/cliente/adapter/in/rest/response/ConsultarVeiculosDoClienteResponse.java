package br.com.fiap.oficina.cliente.adapter.in.rest.response;

import br.com.fiap.oficina.veiculo.domain.model.Veiculo;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsultarVeiculosDoClienteResponse(
        UUID id,
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static ConsultarVeiculosDoClienteResponse from(Veiculo veiculo) {
        return new ConsultarVeiculosDoClienteResponse(
                veiculo.id().value(),
                veiculo.clienteId().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano(),
                veiculo.criadoEm(),
                veiculo.atualizadoEm()
        );
    }
}
