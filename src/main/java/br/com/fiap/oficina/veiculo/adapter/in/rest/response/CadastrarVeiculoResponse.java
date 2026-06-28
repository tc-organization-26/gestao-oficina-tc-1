package br.com.fiap.oficina.veiculo.adapter.in.rest.response;

import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import java.util.UUID;

public record CadastrarVeiculoResponse(
        UUID id,
        String placa,
        String marca,
        String modelo,
        int ano
) {
    public static CadastrarVeiculoResponse from(Veiculo veiculo) {
        return new CadastrarVeiculoResponse(
                veiculo.id().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano()
        );
    }
}
