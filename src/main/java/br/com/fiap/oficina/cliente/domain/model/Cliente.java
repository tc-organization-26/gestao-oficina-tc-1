package br.com.fiap.oficina.cliente.domain.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.shared.domain.Entity;

public final class Cliente extends Entity<ClienteId> {

    private final ClienteId id;
    private final CpfCnpj cpfCnpj;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime criadoEm;

    public Cliente(
            ClienteId id,
            CpfCnpj cpfCnpj,
            String nome,
            String email,
            String telefone,
            boolean ativo,
            OffsetDateTime atualizadoEm,
            OffsetDateTime criadoEm) {
        validarDados(cpfCnpj, nome, email, telefone);

        if (id == null) {
            throw new DomainException("Id do cliente é obrigatório.");
        }

        this.id = id;
        this.cpfCnpj = cpfCnpj;
        this.nome = nome.trim();
        this.email = normalizarEmail(email);
        this.telefone = telefone.trim();
        this.ativo = ativo;
        this.atualizadoEm = atualizadoEm;
        this.criadoEm = criadoEm;
    }

    public static Cliente criar(
            CpfCnpj cpfCnpj,
            String nome,
            String email,
            String telefone) {

        return new Cliente(
                ClienteId.novo(),
                cpfCnpj,
                nome,
                email,
                telefone,
                true,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void atualizar(String nome, String email, String telefone) {
        validarDados(this.cpfCnpj, nome, email, telefone);

        this.nome = nome.trim();
        this.email = normalizarEmail(email);
        this.telefone = telefone.trim();
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static void validarDados(CpfCnpj cpfCnpj, String nome, String email, String telefone) {
        if (cpfCnpj == null) {
            throw new DomainException("CPF/CNPJ é obrigatório.");
        }

        if (nome == null || nome.isBlank()) {
            throw new DomainException("Nome é obrigatório.");
        }

        if (email != null && !email.isBlank() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new DomainException("E-mail inválido.");
        }

        if (telefone == null || telefone.isBlank()) {
            throw new DomainException("Telefone é obrigatório.");
        }
    }

    private static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    @Override
    public ClienteId id() {
        return id;
    }

    public CpfCnpj cpfCnpj() {
        return cpfCnpj;
    }

    public String nome() {
        return nome;
    }

    public String email() {
        return email;
    }

    public String telefone() {
        return telefone;
    }

    public boolean ativo() {
        return ativo;
    }

    public OffsetDateTime atualizadoEm() {
        return atualizadoEm;
    }

    public OffsetDateTime criadoEm() {
        return criadoEm;
    }
}
