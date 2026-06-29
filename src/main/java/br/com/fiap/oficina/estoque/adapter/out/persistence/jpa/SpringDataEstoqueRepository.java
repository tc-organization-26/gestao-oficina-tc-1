package br.com.fiap.oficina.estoque.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataEstoqueRepository extends JpaRepository<ItemEstoqueJpaEntity, UUID> {
    boolean existsByCodigo(String codigo);
    Optional<ItemEstoqueJpaEntity> findByCodigo(String codigo);
    List<ItemEstoqueJpaEntity> findAllByAtivoTrue();
}