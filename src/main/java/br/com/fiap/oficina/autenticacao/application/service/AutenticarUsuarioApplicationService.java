package br.com.fiap.oficina.autenticacao.application.service;

import br.com.fiap.oficina.autenticacao.application.command.AutenticarUsuarioCommand;
import br.com.fiap.oficina.autenticacao.application.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.port.out.GerarTokenPort;
import br.com.fiap.oficina.autenticacao.application.port.out.UsuarioRepositoryPort;
import br.com.fiap.oficina.autenticacao.application.port.out.VerificarSenhaPort;
import br.com.fiap.oficina.shared.domain.DomainException;

public class AutenticarUsuarioApplicationService implements AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final VerificarSenhaPort verificarSenhaPort;
    private final GerarTokenPort gerarTokenPort;

    public AutenticarUsuarioApplicationService(
            UsuarioRepositoryPort usuarioRepository,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenPort gerarTokenPort) {
        this.usuarioRepository = usuarioRepository;
        this.verificarSenhaPort = verificarSenhaPort;
        this.gerarTokenPort = gerarTokenPort;
    }

    @Override
    public String autenticar(AutenticarUsuarioCommand command) {
        var usuario = usuarioRepository.buscarPorLogin(command.login())
                .orElseThrow(() -> new DomainException("Usuario ou senha invalidos."));

        if (!usuario.ativo() || !verificarSenhaPort.verificar(command.senha(), usuario.credencial().senhaHash())) {
            throw new DomainException("Usuario ou senha invalidos.");
        }

        return gerarTokenPort.gerarToken(usuario);
    }
}