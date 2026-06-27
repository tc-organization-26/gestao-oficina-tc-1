package br.com.fiap.oficina.servico.config;

import br.com.fiap.oficina.servico.adapter.out.persistence.jpa.ServicoPersistenceAdapter;
import br.com.fiap.oficina.servico.adapter.out.persistence.jpa.SpringDataServicoRepository;
import br.com.fiap.oficina.servico.application.port.in.AtualizarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.CadastrarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarTodosServicosUseCase;
import br.com.fiap.oficina.servico.application.port.in.ExcluirServicoUseCase;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.application.service.ServicoApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicoConfiguration {

    @Bean
    public CadastrarServicoUseCase cadastrarServicoUseCase(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public AtualizarServicoUseCase atualizarServicoUseCase(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public ConsultarServicoUseCase consultarServicoUseCase(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public ConsultarTodosServicosUseCase consultarTodosServicosUseCase(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public ExcluirServicoUseCase excluirServicoUseCase(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public ServicoRepositoryPort servicoRepositoryPort(SpringDataServicoRepository springDataRepository) {
        return new ServicoPersistenceAdapter(springDataRepository);
    }
}