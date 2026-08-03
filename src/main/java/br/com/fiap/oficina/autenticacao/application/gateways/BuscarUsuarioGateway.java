package br.com.fiap.oficina.autenticacao.application.gateways;

import br.com.fiap.oficina.autenticacao.domain.entities.Usuario;

import java.util.Optional;

public interface BuscarUsuarioGateway {
    Optional<Usuario> buscarPorLogin(String login);
}
