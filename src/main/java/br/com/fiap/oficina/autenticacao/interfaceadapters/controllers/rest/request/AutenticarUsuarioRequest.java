package br.com.fiap.oficina.autenticacao.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.NotBlank;

public record AutenticarUsuarioRequest(
        @NotBlank(message = "Login e obrigatorio.") String login,
        @NotBlank(message = "Senha e obrigatoria.") String senha
) {
}