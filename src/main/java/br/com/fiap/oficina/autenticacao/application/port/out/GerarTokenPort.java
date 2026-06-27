package br.com.fiap.oficina.autenticacao.application.port.out;

import br.com.fiap.oficina.autenticacao.domain.model.Usuario;

public interface GerarTokenPort {
    String gerarToken(Usuario usuario);
}