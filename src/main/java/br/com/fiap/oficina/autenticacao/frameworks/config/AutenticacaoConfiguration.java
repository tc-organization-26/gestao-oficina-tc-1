package br.com.fiap.oficina.autenticacao.frameworks.config;

import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.usuario.UsuarioFicticioAdapter;
import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security.JwtTokenAdapter;
import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security.PasswordEncoderAdapter;
import br.com.fiap.oficina.autenticacao.application.usecases.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.usecases.ValidarTokenUseCase;
import br.com.fiap.oficina.autenticacao.application.gateways.BuscarUsuarioPort;
import br.com.fiap.oficina.autenticacao.application.gateways.GerarTokenPort;
import br.com.fiap.oficina.autenticacao.application.gateways.ValidarTokenPort;
import br.com.fiap.oficina.autenticacao.application.gateways.VerificarSenhaPort;
import br.com.fiap.oficina.autenticacao.application.usecases.interactors.AutenticarUsuarioApplicationService;
import br.com.fiap.oficina.autenticacao.application.usecases.interactors.ValidarTokenApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AutenticacaoConfiguration {
    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(
            BuscarUsuarioPort buscarUsuarioPort,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenPort gerarTokenPort) {
        return new AutenticarUsuarioApplicationService(buscarUsuarioPort, verificarSenhaPort, gerarTokenPort);
    }

    @Bean
    public BuscarUsuarioPort buscarUsuarioPort() {
        var adminSenhaHash = new BCryptPasswordEncoder().encode("ad@456");
        return new UsuarioFicticioAdapter(adminSenhaHash);
    }

    @Bean
    public ValidarTokenUseCase validarTokenUseCase(ValidarTokenPort validarTokenPort) {
        return new ValidarTokenApplicationService(validarTokenPort);
    }

    @Bean
    public VerificarSenhaPort verificarSenhaPort() {
        return new PasswordEncoderAdapter();
    }

    @Bean
    public JwtTokenAdapter jwtTokenAdapter(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        return new JwtTokenAdapter(secret, expirationSeconds);
    }

    @Bean
    public GerarTokenPort gerarTokenPort(JwtTokenAdapter jwtTokenAdapter) {
        return jwtTokenAdapter;
    }

    @Bean
    public ValidarTokenPort validarTokenPort(JwtTokenAdapter jwtTokenAdapter) {
        return jwtTokenAdapter;
    }
}
