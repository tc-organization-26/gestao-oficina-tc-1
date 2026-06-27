package br.com.fiap.oficina.autenticacao.application.port.in;

import br.com.fiap.oficina.autenticacao.application.command.AutenticarUsuarioCommand;

public interface AutenticarUsuarioUseCase {
    String autenticar(AutenticarUsuarioCommand command);
}