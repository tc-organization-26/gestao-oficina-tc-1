package br.com.fiap.oficina.autenticacao.application.port.out;

import br.com.fiap.oficina.autenticacao.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorLogin(String login);
}