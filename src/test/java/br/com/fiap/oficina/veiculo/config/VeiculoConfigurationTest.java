package br.com.fiap.oficina.veiculo.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa.SpringDataVeiculoRepository;
import br.com.fiap.oficina.veiculo.application.port.out.VeiculoRepositoryPort;
import org.junit.jupiter.api.Test;

class VeiculoConfigurationTest {
    @Test
    void criaBeansDoContexto() {
        var config = new VeiculoConfiguration();
        VeiculoRepositoryPort repositoryPort = config.veiculoRepositoryPort(mock(SpringDataVeiculoRepository.class));

        assertNotNull(config.cadastrarVeiculoUseCase(repositoryPort));
        assertNotNull(config.atualizarVeiculoUseCase(repositoryPort));
        assertNotNull(config.consultarVeiculoUseCase(repositoryPort));
        assertNotNull(repositoryPort);
    }
}