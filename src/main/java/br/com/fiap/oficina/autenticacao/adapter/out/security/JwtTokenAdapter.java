package br.com.fiap.oficina.autenticacao.adapter.out.security;

import br.com.fiap.oficina.autenticacao.application.port.out.GerarTokenPort;
import br.com.fiap.oficina.autenticacao.domain.model.Usuario;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtTokenAdapter implements GerarTokenPort {
    @Override
    public String gerarToken(Usuario usuario) {
        var payload = usuario.id().value() + ":" + usuario.papel().name();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }
}