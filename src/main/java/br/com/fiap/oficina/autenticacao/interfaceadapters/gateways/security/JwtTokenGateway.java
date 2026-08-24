package br.com.fiap.oficina.autenticacao.interfaceadapters.gateways.security;

import br.com.fiap.oficina.autenticacao.application.gateways.GerarTokenGateway;
import br.com.fiap.oficina.autenticacao.application.gateways.ValidarTokenGateway;
import br.com.fiap.oficina.autenticacao.domain.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class JwtTokenGateway implements GerarTokenGateway, ValidarTokenGateway {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenGateway(String secret, long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String gerarToken(Usuario usuario) {
        var agora = Instant.now();
        var expiraEm = agora.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .subject(usuario.id().value().toString())
                .claim("papel", usuario.papel().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiraEm))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Optional<TokenAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            var usuarioId = UUID.fromString(claims.getSubject());
            var papel = br.com.fiap.oficina.autenticacao.domain.enums.Papel.valueOf(claims.get("papel", String.class));
            return Optional.of(new TokenAutenticado(usuarioId, papel));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
