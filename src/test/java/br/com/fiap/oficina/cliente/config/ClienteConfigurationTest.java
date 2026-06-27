package br.com.fiap.oficina.cliente.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import br.com.fiap.oficina.cliente.adapter.out.persistence.jpa.SpringDataClienteRepository;
import br.com.fiap.oficina.cliente.application.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.Test;

class ClienteConfigurationTest {
    @Test
    void criaBeansDoContexto() {
        var config = new ClienteConfiguration();
        ClienteRepositoryPort repositoryPort = config.clienteRepositoryPort(mock(SpringDataClienteRepository.class));

        assertNotNull(config.cadastrarClienteUseCase(repositoryPort));
        assertNotNull(config.atualizarClienteUseCase(repositoryPort));
        assertNotNull(config.consultarClienteUseCase(repositoryPort));
        assertNotNull(repositoryPort);
    }
}