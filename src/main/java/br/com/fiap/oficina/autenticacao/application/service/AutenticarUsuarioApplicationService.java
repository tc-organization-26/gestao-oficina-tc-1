package br.com.fiap.oficina.autenticacao.application.service;

import br.com.fiap.oficina.autenticacao.application.command.AutenticarUsuarioCommand;
import br.com.fiap.oficina.autenticacao.application.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.port.out.BuscarUsuarioPort;
import br.com.fiap.oficina.autenticacao.application.port.out.GerarTokenPort;
import br.com.fiap.oficina.autenticacao.application.port.out.VerificarSenhaPort;
import br.com.fiap.oficina.shared.domain.DomainException;

public class AutenticarUsuarioApplicationService implements AutenticarUsuarioUseCase {

    private final BuscarUsuarioPort buscarUsuarioPort;
    private final VerificarSenhaPort verificarSenhaPort;
    private final GerarTokenPort gerarTokenPort;

    public AutenticarUsuarioApplicationService(
            BuscarUsuarioPort buscarUsuarioPort,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenPort gerarTokenPort) {
        this.buscarUsuarioPort = buscarUsuarioPort;
        this.verificarSenhaPort = verificarSenhaPort;
        this.gerarTokenPort = gerarTokenPort;
    }

    @Override
    public String autenticar(AutenticarUsuarioCommand command) {
        var usuario = buscarUsuarioPort.buscarPorLogin(command.login())
                .orElseThrow(() -> new DomainException("Usuario ou senha invalidos."));

        if (!usuario.ativo() || !verificarSenhaPort.verificar(command.senha(), usuario.credencial().senhaHash())) {
            throw new DomainException("Usuario ou senha invalidos.");
        }

        return gerarTokenPort.gerarToken(usuario);
    }
}
