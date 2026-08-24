package br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOrdemServicoRepository extends JpaRepository<OrdemServicoJpaEntity, UUID> {
	List<OrdemServicoJpaEntity> findAllByClienteIdOrderByDataRecebimentoAsc(UUID clienteId);
	List<OrdemServicoJpaEntity> findAllByStatusOrdemServicoOrderByDataRecebimentoAsc(Integer statusOrdemServico);
	List<OrdemServicoJpaEntity> findAllByOrderByDataRecebimentoAsc();
}