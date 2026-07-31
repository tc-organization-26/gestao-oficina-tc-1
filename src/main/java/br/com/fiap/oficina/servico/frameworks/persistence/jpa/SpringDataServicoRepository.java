package br.com.fiap.oficina.servico.frameworks.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataServicoRepository
        extends JpaRepository<ServicoJpaEntity, UUID> {

    boolean existsByCodigo(String codigo);

    Optional<ServicoJpaEntity> findByCodigo(String codigo);
}