package br.com.fiap.oficina.estoque.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataEstoqueRepository extends JpaRepository<ItemEstoqueJpaEntity, UUID> {
    boolean existsByCodigo(String codigo);
}