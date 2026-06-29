package br.com.fiap.oficina.autenticacao.application.port.out;

import br.com.fiap.oficina.autenticacao.domain.model.Papel;

import java.util.Optional;
import java.util.UUID;

public interface ValidarTokenPort {
    Optional<TokenAutenticado> validar(String token);

    record TokenAutenticado(UUID usuarioId, Papel papel) {
    }
}