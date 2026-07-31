package br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security;

import br.com.fiap.oficina.autenticacao.application.gateways.VerificarSenhaPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderAdapter implements VerificarSenhaPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean verificar(String senha, String senhaHash) {
        return encoder.matches(senha, senhaHash);
    }
}