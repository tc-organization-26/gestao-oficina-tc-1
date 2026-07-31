package br.com.fiap.oficina.cliente.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarClienteRequest(

        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Telefone é obrigatório.")
        String telefone
) {
}