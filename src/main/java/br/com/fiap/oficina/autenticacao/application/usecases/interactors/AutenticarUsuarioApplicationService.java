package br.com.fiap.oficina.autenticacao.application.usecases.interactors;

import br.com.fiap.oficina.autenticacao.application.dtos.AutenticarUsuarioCommand;
import br.com.fiap.oficina.autenticacao.application.usecases.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.gateways.BuscarUsuarioGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.GerarTokenGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.VerificarSenhaGateway;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

public class AutenticarUsuarioApplicationService implements AutenticarUsuarioUseCase {

    private final BuscarUsuarioGateway buscarUsuarioGateway;
    private final VerificarSenhaGateway verificarSenhaGateway;
    private final GerarTokenGateway gerarTokenGateway;

    public AutenticarUsuarioApplicationService(
            BuscarUsuarioGateway buscarUsuarioGateway,
            VerificarSenhaGateway verificarSenhaGateway,
            GerarTokenGateway gerarTokenGateway) {
        this.buscarUsuarioGateway = buscarUsuarioGateway;
        this.verificarSenhaGateway = verificarSenhaGateway;
        this.gerarTokenGateway = gerarTokenGateway;
    }

    @Override
    public String autenticar(AutenticarUsuarioCommand command) {
        var usuario = buscarUsuarioGateway.buscarPorLogin(command.login())
                .orElseThrow(() -> new DomainException("Usuario ou senha invalidos."));

        if (!usuario.ativo() || !verificarSenhaGateway.verificar(command.senha(), usuario.credencial().senhaHash())) {
            throw new DomainException("Usuario ou senha invalidos.");
        }

        return gerarTokenGateway.gerarToken(usuario);
    }
}
