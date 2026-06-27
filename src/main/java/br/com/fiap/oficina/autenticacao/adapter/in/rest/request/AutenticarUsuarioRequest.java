package br.com.fiap.oficina.autenticacao.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;

public record AutenticarUsuarioRequest(
        @NotBlank(message = "Login e obrigatorio.") String login,
        @NotBlank(message = "Senha e obrigatoria.") String senha
) {
}