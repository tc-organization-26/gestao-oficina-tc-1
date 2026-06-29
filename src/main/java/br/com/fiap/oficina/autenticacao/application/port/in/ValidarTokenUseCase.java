package br.com.fiap.oficina.autenticacao.application.port.in;

import br.com.fiap.oficina.autenticacao.domain.model.Papel;

import java.util.Optional;
import java.util.UUID;

public interface ValidarTokenUseCase {
    Optional<TokenAutenticado> validar(String token);

    record TokenAutenticado(UUID usuarioId, Papel papel) {
    }
}
