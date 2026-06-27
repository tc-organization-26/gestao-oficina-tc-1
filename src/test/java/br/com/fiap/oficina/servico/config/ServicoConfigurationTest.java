package br.com.fiap.oficina.servico.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import br.com.fiap.oficina.servico.adapter.out.persistence.jpa.SpringDataServicoRepository;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;
import org.junit.jupiter.api.Test;

class ServicoConfigurationTest {
    @Test
    void criaBeansDoContexto() {
        var config = new ServicoConfiguration();
        ServicoRepositoryPort repositoryPort = config.servicoRepositoryPort(mock(SpringDataServicoRepository.class));

        assertNotNull(config.cadastrarServicoUseCase(repositoryPort));
        assertNotNull(config.atualizarServicoUseCase(repositoryPort));
        assertNotNull(config.consultarServicoUseCase(repositoryPort));
        assertNotNull(repositoryPort);
    }
}