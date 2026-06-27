package br.com.fiap.oficina.autenticacao.config;

import br.com.fiap.oficina.autenticacao.adapter.out.persistence.jpa.UsuarioPersistenceAdapter;
import br.com.fiap.oficina.autenticacao.adapter.out.security.JwtTokenAdapter;
import br.com.fiap.oficina.autenticacao.adapter.out.security.PasswordEncoderAdapter;
import br.com.fiap.oficina.autenticacao.application.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.port.out.GerarTokenPort;
import br.com.fiap.oficina.autenticacao.application.port.out.UsuarioRepositoryPort;
import br.com.fiap.oficina.autenticacao.application.port.out.VerificarSenhaPort;
import br.com.fiap.oficina.autenticacao.application.service.AutenticarUsuarioApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutenticacaoConfiguration {
    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(
            UsuarioRepositoryPort usuarioRepositoryPort,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenPort gerarTokenPort) {
        return new AutenticarUsuarioApplicationService(usuarioRepositoryPort, verificarSenhaPort, gerarTokenPort);
    }

    @Bean
    public UsuarioRepositoryPort usuarioRepositoryPort() {
        return new UsuarioPersistenceAdapter();
    }

    @Bean
    public VerificarSenhaPort verificarSenhaPort() {
        return new PasswordEncoderAdapter();
    }

    @Bean
    public GerarTokenPort gerarTokenPort() {
        return new JwtTokenAdapter();
    }
}