package br.com.fiap.oficina.servico.config;

import br.com.fiap.oficina.servico.adapter.out.persistence.jpa.ServicoPersistenceAdapter;
import br.com.fiap.oficina.servico.adapter.out.persistence.jpa.SpringDataServicoRepository;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.application.service.ServicoApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicoConfiguration {

    @Bean(name = {
            "cadastrarServicoUseCase",
            "atualizarServicoUseCase",
            "consultarServicoUseCase",
            "consultarTodosServicosUseCase",
            "excluirServicoUseCase"
    })
    public ServicoApplicationService servicoApplicationService(ServicoRepositoryPort servicoRepositoryPort) {
        return new ServicoApplicationService(servicoRepositoryPort);
    }

    @Bean
    public ServicoRepositoryPort servicoRepositoryPort(SpringDataServicoRepository springDataRepository) {
        return new ServicoPersistenceAdapter(springDataRepository);
    }
}
