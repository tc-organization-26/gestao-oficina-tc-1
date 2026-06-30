package br.com.fiap.oficina.cliente.config;

import br.com.fiap.oficina.cliente.adapter.out.persistence.jpa.ClientePersistenceAdapter;
import br.com.fiap.oficina.cliente.adapter.out.persistence.jpa.SpringDataClienteRepository;
import br.com.fiap.oficina.cliente.application.port.out.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.application.service.ClienteApplicationService;
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
