package br.com.fiap.oficina.autenticacao.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;

public record Credencial(String login, String senhaHash) {
    public Credencial {
        if (login == null || login.isBlank()) {
            throw new DomainException("Login e obrigatorio.");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new DomainException("Senha e obrigatoria.");
        }
        login = login.trim().toLowerCase();
    }
}