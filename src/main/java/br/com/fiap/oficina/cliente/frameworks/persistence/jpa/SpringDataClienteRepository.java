package br.com.fiap.oficina.cliente.frameworks.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataClienteRepository
        extends JpaRepository<ClienteJpaEntity, UUID> {

    boolean existsByCpfCnpj(String cpfCnpj);

    Optional<ClienteJpaEntity> findByCpfCnpj(String cpfCnpj);
}