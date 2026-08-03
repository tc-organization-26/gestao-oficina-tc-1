package br.com.fiap.oficina.cliente.frameworks.config;

import br.com.fiap.oficina.cliente.interfaceadapters.gateways.persistence.jpa.ClienteJpaGateway;
import br.com.fiap.oficina.cliente.frameworks.persistence.jpa.SpringDataClienteRepository;
import br.com.fiap.oficina.cliente.application.gateways.ClienteGateway;
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
    public ClienteApplicationService clienteApplicationService(ClienteGateway clienteGateway) {
        return new ClienteApplicationService(clienteGateway);
    }

    @Bean
    public ClienteGateway clienteGateway(SpringDataClienteRepository springDataRepository) {
        return new ClienteJpaGateway(springDataRepository);
    }
}
