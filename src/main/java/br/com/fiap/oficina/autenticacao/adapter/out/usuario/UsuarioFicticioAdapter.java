package br.com.fiap.oficina.autenticacao.adapter.out.usuario;

import br.com.fiap.oficina.autenticacao.application.port.out.BuscarUsuarioPort;
import br.com.fiap.oficina.autenticacao.domain.model.Credencial;
import br.com.fiap.oficina.autenticacao.domain.model.Papel;
import br.com.fiap.oficina.autenticacao.domain.model.Usuario;
import br.com.fiap.oficina.autenticacao.domain.model.UsuarioId;

import java.util.Optional;
import java.util.UUID;

public class UsuarioFicticioAdapter implements BuscarUsuarioPort {

    private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final String adminSenhaHash;

    public UsuarioFicticioAdapter(String adminSenhaHash) {
        this.adminSenhaHash = adminSenhaHash;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        if (!"admin".equalsIgnoreCase(login)) {
            return Optional.empty();
        }
        return Optional.of(new Usuario(
                new UsuarioId(ADMIN_ID),
                new Credencial("admin", adminSenhaHash),
                Papel.ADMINISTRADOR,
                true));
    }
}
