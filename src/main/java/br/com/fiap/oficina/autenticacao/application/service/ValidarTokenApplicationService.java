package br.com.fiap.oficina.autenticacao.application.service;

import br.com.fiap.oficina.autenticacao.application.port.in.ValidarTokenUseCase;
import br.com.fiap.oficina.autenticacao.application.port.out.ValidarTokenPort;

import java.util.Optional;

public class ValidarTokenApplicationService implements ValidarTokenUseCase {

    private final ValidarTokenPort validarTokenPort;

    public ValidarTokenApplicationService(ValidarTokenPort validarTokenPort) {
        this.validarTokenPort = validarTokenPort;
    }

    @Override
    public Optional<TokenAutenticado> validar(String token) {
        return validarTokenPort.validar(token)
                .map(tokenAutenticado -> new TokenAutenticado(
                        tokenAutenticado.usuarioId(),
                        tokenAutenticado.papel()));
    }
}
