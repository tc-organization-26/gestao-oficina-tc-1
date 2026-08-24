package br.com.fiap.oficina.veiculo.frameworks.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataVeiculoRepository
        extends JpaRepository<VeiculoJpaEntity, UUID> {

    boolean existsByPlaca(String placa);

    List<VeiculoJpaEntity> findByClienteId(UUID clienteId);
}