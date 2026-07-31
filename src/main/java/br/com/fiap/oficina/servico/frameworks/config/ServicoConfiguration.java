package br.com.fiap.oficina.servico.frameworks.config;

import br.com.fiap.oficina.servico.interfaceadapters.gateways.persistence.jpa.ServicoPersistenceAdapter;
import br.com.fiap.oficina.servico.frameworks.persistence.jpa.SpringDataServicoRepository;
import br.com.fiap.oficina.servico.application.gateways.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.application.usecases.interactors.ServicoApplicationService;
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
