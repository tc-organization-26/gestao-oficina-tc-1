package br.com.fiap.oficina.autenticacao.application.gateways;

public interface VerificarSenhaPort {
    boolean verificar(String senha, String senhaHash);
}