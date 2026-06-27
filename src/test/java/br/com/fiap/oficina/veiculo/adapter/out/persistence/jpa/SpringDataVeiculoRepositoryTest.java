package br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

class SpringDataVeiculoRepositoryTest {
    @Test
    void estendeJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(SpringDataVeiculoRepository.class));
    }
}