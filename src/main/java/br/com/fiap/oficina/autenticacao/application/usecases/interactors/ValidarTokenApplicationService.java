package br.com.fiap.oficina.autenticacao.application.usecases.interactors;

import br.com.fiap.oficina.autenticacao.application.usecases.ValidarTokenUseCase;
import br.com.fiap.oficina.autenticacao.application.gateways.ValidarTokenGateway;

import java.util.Optional;

public class ValidarTokenApplicationService implements ValidarTokenUseCase {

    private final ValidarTokenGateway validarTokenGateway;

    public ValidarTokenApplicationService(ValidarTokenGateway validarTokenGateway) {
        this.validarTokenGateway = validarTokenGateway;
    }

    @Override
    public Optional<TokenAutenticado> validar(String token) {
        return validarTokenGateway.validar(token)
                .map(tokenAutenticado -> new TokenAutenticado(
                        tokenAutenticado.usuarioId(),
                        tokenAutenticado.papel()));
    }
}
