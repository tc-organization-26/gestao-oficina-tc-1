package br.com.fiap.oficina.cliente.application.command;

public record CadastrarClienteCommand (
        String nome,
        String cpfCnpj,
        String email,
        String telefone
) {
}
