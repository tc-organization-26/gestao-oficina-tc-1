package br.com.fiap.oficina.autenticacao.application.gateways;

import br.com.fiap.oficina.autenticacao.domain.entities.Usuario;

public interface GerarTokenGateway {
    String gerarToken(Usuario usuario);
}