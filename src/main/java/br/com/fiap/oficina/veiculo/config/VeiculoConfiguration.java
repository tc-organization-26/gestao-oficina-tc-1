package br.com.fiap.oficina.veiculo.config;

import br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa.SpringDataVeiculoRepository;
import br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa.VeiculoPersistenceAdapter;
import br.com.fiap.oficina.veiculo.application.port.out.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.application.service.VeiculoApplicationService;
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
