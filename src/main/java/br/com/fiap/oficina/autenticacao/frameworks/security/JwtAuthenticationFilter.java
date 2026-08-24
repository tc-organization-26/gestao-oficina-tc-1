package br.com.fiap.oficina.autenticacao.frameworks.security;

import br.com.fiap.oficina.autenticacao.application.usecases.ValidarTokenUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ValidarTokenUseCase validarTokenUseCase;

    public JwtAuthenticationFilter(ValidarTokenUseCase validarTokenUseCase) {
        this.validarTokenUseCase = validarTokenUseCase;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring(7);
            var tokenAutenticado = validarTokenUseCase.validar(token);

            if (tokenAutenticado.isPresent()) {
                var autenticado = tokenAutenticado.get();
                var authority = new SimpleGrantedAuthority("ROLE_" + autenticado.papel().name());
                var authentication = new UsernamePasswordAuthenticationToken(
                        autenticado.usuarioId().toString(),
                        null,
                        List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
