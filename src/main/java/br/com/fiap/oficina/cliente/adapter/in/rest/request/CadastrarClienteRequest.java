package br.com.fiap.oficina.cliente.adapter.in.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarClienteRequest(

        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @NotBlank(message = "CPF/CNPJ é obrigatório.")
        String cpfCnpj,

        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Telefone é obrigatório.")
        String telefone
) {
}