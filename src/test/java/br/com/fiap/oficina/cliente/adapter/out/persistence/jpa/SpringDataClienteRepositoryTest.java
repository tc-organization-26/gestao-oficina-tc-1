package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

class SpringDataClienteRepositoryTest {
    @Test
    void estendeJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(SpringDataClienteRepository.class));
    }
}