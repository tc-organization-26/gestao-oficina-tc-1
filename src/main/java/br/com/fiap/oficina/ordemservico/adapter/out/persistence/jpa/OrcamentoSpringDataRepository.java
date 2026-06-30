package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OrcamentoSpringDataRepository extends JpaRepository<OrcamentoJpaEntity, UUID> {
    Optional<OrcamentoJpaEntity> findByOrdemServicoId(UUID ordemServicoId);
}