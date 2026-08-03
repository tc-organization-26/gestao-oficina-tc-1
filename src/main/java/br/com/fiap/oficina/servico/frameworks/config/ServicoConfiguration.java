package br.com.fiap.oficina.servico.frameworks.config;

import br.com.fiap.oficina.servico.interfaceadapters.gateways.persistence.jpa.ServicoJpaGateway;
import br.com.fiap.oficina.servico.frameworks.persistence.jpa.SpringDataServicoRepository;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
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
    public ServicoApplicationService servicoApplicationService(ServicoGateway servicoGateway) {
        return new ServicoApplicationService(servicoGateway);
    }

    @Bean
    public ServicoGateway servicoGateway(SpringDataServicoRepository springDataRepository) {
        return new ServicoJpaGateway(springDataRepository);
    }
}
