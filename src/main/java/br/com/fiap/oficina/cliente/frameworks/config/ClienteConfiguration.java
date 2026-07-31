package br.com.fiap.oficina.cliente.frameworks.config;

import br.com.fiap.oficina.cliente.interfaceadapters.gateways.persistence.jpa.ClientePersistenceAdapter;
import br.com.fiap.oficina.cliente.frameworks.persistence.jpa.SpringDataClienteRepository;
import br.com.fiap.oficina.cliente.application.gateways.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.application.usecases.interactors.ClienteApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfiguration {

    @Bean(name = {
            "cadastrarClienteUseCase",
            "atualizarClienteUseCase",
            "consultarClienteUseCase",
            "consultarTodosClientesUseCase",
            "excluirClienteUseCase"
    })
    public ClienteApplicationService clienteApplicationService(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public ClienteRepositoryPort clienteRepositoryPort(SpringDataClienteRepository springDataRepository) {
        return new ClientePersistenceAdapter(springDataRepository);
    }
}
