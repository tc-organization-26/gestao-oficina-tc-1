package br.com.fiap.oficina.autenticacao.application.command;

public record AutenticarUsuarioCommand(String login, String senha) {
}