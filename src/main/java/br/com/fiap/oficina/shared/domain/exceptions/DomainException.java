package br.com.fiap.oficina.shared.domain.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}
