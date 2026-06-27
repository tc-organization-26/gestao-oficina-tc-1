package br.com.fiap.oficina.autenticacao.adapter.out.persistence.jpa;

import br.com.fiap.oficina.autenticacao.application.port.out.UsuarioRepositoryPort;
import br.com.fiap.oficina.autenticacao.domain.model.Usuario;

import java.util.Optional;

public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {
    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return Optional.empty();
    }
}