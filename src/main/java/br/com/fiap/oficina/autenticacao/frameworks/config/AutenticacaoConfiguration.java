package br.com.fiap.oficina.autenticacao.frameworks.config;

import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.usuario.UsuarioFicticioGateway;
import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security.JwtTokenGateway;
import br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security.BCryptSenhaGateway;
import br.com.fiap.oficina.autenticacao.application.usecases.AutenticarUsuarioUseCase;
import br.com.fiap.oficina.autenticacao.application.usecases.ValidarTokenUseCase;
import br.com.fiap.oficina.autenticacao.application.gateways.BuscarUsuarioGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.GerarTokenGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.ValidarTokenGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.VerificarSenhaGateway;
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
            BuscarUsuarioGateway buscarUsuarioGateway,
            VerificarSenhaGateway verificarSenhaGateway,
            GerarTokenGateway gerarTokenGateway) {
        return new AutenticarUsuarioApplicationService(buscarUsuarioGateway, verificarSenhaGateway, gerarTokenGateway);
    }

    @Bean
    public BuscarUsuarioGateway buscarUsuarioGateway() {
        var adminSenhaHash = new BCryptPasswordEncoder().encode("ad@456");
        return new UsuarioFicticioGateway(adminSenhaHash);
    }

    @Bean
    public ValidarTokenUseCase validarTokenUseCase(ValidarTokenGateway validarTokenGateway) {
        return new ValidarTokenApplicationService(validarTokenGateway);
    }

    @Bean
    public VerificarSenhaGateway verificarSenhaGateway() {
        return new BCryptSenhaGateway();
    }

    @Bean
    public JwtTokenGateway jwtTokenGateway(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        return new JwtTokenGateway(secret, expirationSeconds);
    }

    @Bean
    public GerarTokenGateway gerarTokenGateway(JwtTokenGateway jwtTokenGateway) {
        return jwtTokenGateway;
    }

    @Bean
    public ValidarTokenGateway validarTokenGateway(JwtTokenGateway jwtTokenGateway) {
        return jwtTokenGateway;
    }
}
