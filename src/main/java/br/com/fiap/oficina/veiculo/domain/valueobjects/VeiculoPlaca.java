package br.com.fiap.oficina.veiculo.domain.valueobjects;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

public record VeiculoPlaca(String value) {

    public VeiculoPlaca {
        if (value == null || value.isBlank()) {
            throw new DomainException("Placa é obrigatória.");
        }

        value = value
                .trim()
                .toUpperCase()
                .replace("-", "");

        if (!value.matches("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$")) {
            throw new DomainException("Placa inválida.");
        }
    }

    public static VeiculoPlaca novo(String value) {
        return new VeiculoPlaca(value);
    }
}