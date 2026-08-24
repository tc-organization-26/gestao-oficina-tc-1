package br.com.fiap.oficina.autenticacao.application.usecases;

import br.com.fiap.oficina.autenticacao.domain.enums.Papel;

import java.util.Optional;
import java.util.UUID;

public interface ValidarTokenUseCase {
    Optional<TokenAutenticado> validar(String token);

    record TokenAutenticado(UUID usuarioId, Papel papel) {
    }
}
