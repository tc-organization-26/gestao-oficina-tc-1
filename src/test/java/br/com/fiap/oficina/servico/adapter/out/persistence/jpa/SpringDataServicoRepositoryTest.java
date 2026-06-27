package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

class SpringDataServicoRepositoryTest {
    @Test
    void estendeJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(SpringDataServicoRepository.class));
    }
}