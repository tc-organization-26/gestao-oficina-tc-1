package br.com.fiap.oficina.autenticacao.application.usecases;

import br.com.fiap.oficina.autenticacao.application.dtos.AutenticarUsuarioCommand;

public interface AutenticarUsuarioUseCase {
    String autenticar(AutenticarUsuarioCommand command);
}