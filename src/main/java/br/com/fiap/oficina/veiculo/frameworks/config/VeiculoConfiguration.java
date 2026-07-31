package br.com.fiap.oficina.veiculo.frameworks.config;

import br.com.fiap.oficina.veiculo.frameworks.persistence.jpa.SpringDataVeiculoRepository;
import br.com.fiap.oficina.veiculo.interfaceadapters.gateways.persistence.jpa.VeiculoPersistenceAdapter;
import br.com.fiap.oficina.veiculo.application.gateways.VeiculoRepositoryPort;
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
    public VeiculoApplicationService veiculoApplicationService(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public VeiculoRepositoryPort veiculoRepositoryPort(SpringDataVeiculoRepository springDataRepository) {
        return new VeiculoPersistenceAdapter(springDataRepository);
    }
}
