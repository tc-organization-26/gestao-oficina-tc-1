package br.com.fiap.oficina.veiculo.frameworks.config;

import br.com.fiap.oficina.veiculo.frameworks.persistence.jpa.SpringDataVeiculoRepository;
import br.com.fiap.oficina.veiculo.interfaceadapters.gateways.persistence.jpa.VeiculoJpaGateway;
import br.com.fiap.oficina.veiculo.application.gateways.VeiculoGateway;
import br.com.fiap.oficina.veiculo.application.usecases.interactors.VeiculoApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VeiculoConfiguration {

    @Bean(name = {
            "cadastrarVeiculoUseCase",
            "atualizarVeiculoUseCase",
            "consultarVeiculoUseCase",
            "consultarTodosVeiculosUseCase",
            "consultarVeiculosPorClienteUseCase",
            "excluirVeiculoUseCase"
    })
    public VeiculoApplicationService veiculoApplicationService(VeiculoGateway veiculoGateway) {
        return new VeiculoApplicationService(veiculoGateway);
    }

    @Bean
    public VeiculoGateway veiculoGateway(SpringDataVeiculoRepository springDataRepository) {
        return new VeiculoJpaGateway(springDataRepository);
    }
}
