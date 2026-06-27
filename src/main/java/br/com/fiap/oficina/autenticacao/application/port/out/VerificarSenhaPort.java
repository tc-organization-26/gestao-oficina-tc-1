package br.com.fiap.oficina.autenticacao.application.port.out;

public interface VerificarSenhaPort {
    boolean verificar(String senha, String senhaHash);
}