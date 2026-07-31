package br.com.fiap.oficina.autenticacao.domain.entities;

import br.com.fiap.oficina.autenticacao.domain.enums.*;

import br.com.fiap.oficina.autenticacao.domain.valueobjects.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.shared.domain.entities.Entity;

public final class Usuario extends Entity<UsuarioId> {

    private final UsuarioId id;
    private final Credencial credencial;
    private final Papel papel;
    private final boolean ativo;

    public Usuario(UsuarioId id, Credencial credencial, Papel papel, boolean ativo) {
        if (id == null) {
            throw new DomainException("Id do usuario e obrigatorio.");
        }
        if (credencial == null) {
            throw new DomainException("Credencial do usuario e obrigatoria.");
        }
        if (papel == null) {
            throw new DomainException("Papel do usuario e obrigatorio.");
        }
        this.id = id;
        this.credencial = credencial;
        this.papel = papel;
        this.ativo = ativo;
    }

    public static Usuario criar(String login, String senhaHash, Papel papel) {
        return new Usuario(UsuarioId.novo(), new Credencial(login, senhaHash), papel, true);
    }

    @Override public UsuarioId id() { return id; }
    public Credencial credencial() { return credencial; }
    public Papel papel() { return papel; }
    public boolean ativo() { return ativo; }
}