package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrcamentoSpringDataRepository extends JpaRepository<OrcamentoJpaEntity, UUID> {
}
