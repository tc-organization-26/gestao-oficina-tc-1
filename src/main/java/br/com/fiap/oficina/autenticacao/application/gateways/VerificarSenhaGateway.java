package br.com.fiap.oficina.autenticacao.application.gateways;

public interface VerificarSenhaGateway {
    boolean verificar(String senha, String senhaHash);
}