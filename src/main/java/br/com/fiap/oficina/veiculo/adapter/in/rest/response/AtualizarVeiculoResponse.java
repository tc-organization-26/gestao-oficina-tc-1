package br.com.fiap.oficina.veiculo.adapter.in.rest.response;

import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import java.util.UUID;

public record AtualizarVeiculoResponse(
        UUID id,
        String placa,
        String marca,
        String modelo,
        int ano
) {
    public static AtualizarVeiculoResponse from(Veiculo veiculo) {
        return new AtualizarVeiculoResponse(
                veiculo.id().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano()
        );
    }
}
