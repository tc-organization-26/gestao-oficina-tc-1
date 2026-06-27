package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, length = 14)
    private String cpfCnpj;

    @Column(length = 20)
    private String telefone;

    @Column(length = 254)
    private String email;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em")
    private OffsetDateTime atualizadoEm;

    protected ClienteJpaEntity() {
        // Obrigatório para JPA.
    }

    public ClienteJpaEntity(
            UUID id,
            String nome,
            String cpfCnpj,
            String telefone,
            String email,
            boolean ativo,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm
    ) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.email = email;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}