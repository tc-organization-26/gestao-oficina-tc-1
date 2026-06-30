package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataServicoRepository
        extends JpaRepository<ServicoJpaEntity, UUID> {

    boolean existsByCodigo(String codigo);

    Optional<ServicoJpaEntity> findByCodigo(String codigo);
}