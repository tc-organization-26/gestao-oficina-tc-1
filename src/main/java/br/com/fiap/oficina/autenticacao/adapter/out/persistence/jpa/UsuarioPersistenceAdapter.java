package br.com.fiap.oficina.autenticacao.adapter.out.persistence.jpa;

import br.com.fiap.oficina.autenticacao.application.port.out.UsuarioRepositoryPort;
import br.com.fiap.oficina.autenticacao.domain.model.Credencial;
import br.com.fiap.oficina.autenticacao.domain.model.Papel;
import br.com.fiap.oficina.autenticacao.domain.model.Usuario;
import br.com.fiap.oficina.autenticacao.domain.model.UsuarioId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {
    private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        if (!"admin".equalsIgnoreCase(login)) {
            return Optional.empty();
        }
        var senhaHash = new BCryptPasswordEncoder().encode("123456");
        var usuario = new Usuario(
                new UsuarioId(ADMIN_ID),
                new Credencial("admin", senhaHash),
                Papel.ADMINISTRADOR,
                true);
        return Optional.of(usuario);
    }
}