package br.com.fiap.oficina.cliente.config;

import br.com.fiap.oficina.cliente.adapter.out.persistence.jpa.ClientePersistenceAdapter;
import br.com.fiap.oficina.cliente.adapter.out.persistence.jpa.SpringDataClienteRepository;
import br.com.fiap.oficina.cliente.application.port.in.AtualizarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.CadastrarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarTodosClientesUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ExcluirClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.out.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.application.service.ClienteApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClienteConfiguration {

    @Bean
    public CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public AtualizarClienteUseCase atualizarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public ConsultarClienteUseCase consultarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public ConsultarTodosClientesUseCase consultarTodosClientesUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public ExcluirClienteUseCase excluirClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        return new ClienteApplicationService(clienteRepositoryPort);
    }

    @Bean
    public ClienteRepositoryPort clienteRepositoryPort(SpringDataClienteRepository springDataRepository) {
        return new ClientePersistenceAdapter(springDataRepository);
    }
}