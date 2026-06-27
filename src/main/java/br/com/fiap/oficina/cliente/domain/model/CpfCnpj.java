package br.com.fiap.oficina.cliente.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

public record CpfCnpj(String value) {

    public CpfCnpj {
        if (value == null || value.isBlank()) {
            throw new DomainException("CPF/CNPJ é obrigatório.");
        }

        value = value.replaceAll("\\D", "");

        if (value.length() != 11 && value.length() != 14) {
            throw new DomainException("CPF/CNPJ inválido.");
        }
    }

    public static CpfCnpj novo(String value) {
        return new CpfCnpj(value);
    }
}
