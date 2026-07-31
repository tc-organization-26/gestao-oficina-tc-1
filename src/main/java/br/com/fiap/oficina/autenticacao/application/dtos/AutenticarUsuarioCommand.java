package br.com.fiap.oficina.autenticacao.application.dtos;

public record AutenticarUsuarioCommand(String login, String senha) {
}