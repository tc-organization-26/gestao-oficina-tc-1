package br.com.fiap.oficina.autenticacao.application.gateways;

import br.com.fiap.oficina.autenticacao.domain.enums.Papel;

import java.util.Optional;
import java.util.UUID;

public interface ValidarTokenGateway {
    Optional<TokenAutenticado> validar(String token);

    record TokenAutenticado(UUID usuarioId, Papel papel) {
    }
}