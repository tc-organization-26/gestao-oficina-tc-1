package br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security;

import br.com.fiap.oficina.autenticacao.application.gateways.VerificarSenhaGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptSenhaGateway implements VerificarSenhaGateway {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean verificar(String senha, String senhaHash) {
        return encoder.matches(senha, senhaHash);
    }
}
