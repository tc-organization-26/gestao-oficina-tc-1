package br.com.fiap.oficina.cliente.application.dtos;

public record CadastrarClienteCommand (
        String nome,
        String cpfCnpj,
        String email,
        String telefone
) {
}
