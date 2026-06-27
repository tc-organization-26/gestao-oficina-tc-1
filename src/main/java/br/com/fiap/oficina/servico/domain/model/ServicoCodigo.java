package br.com.fiap.oficina.servico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

public record ServicoCodigo(String value) {

    public ServicoCodigo {
        if (value == null || value.isBlank()) {
            throw new DomainException("Código é obrigatório.");
        }
    }

    public static ServicoCodigo novo(String value) {
        return new ServicoCodigo(value);
    }
}