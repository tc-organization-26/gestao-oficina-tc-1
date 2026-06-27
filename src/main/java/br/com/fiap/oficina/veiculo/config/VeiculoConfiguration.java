package br.com.fiap.oficina.veiculo.config;

import br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa.SpringDataVeiculoRepository;
import br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa.VeiculoPersistenceAdapter;
import br.com.fiap.oficina.veiculo.application.port.in.AtualizarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.CadastrarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarTodosVeiculosUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ExcluirVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.out.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.application.service.VeiculoApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VeiculoConfiguration {

    @Bean
    public CadastrarVeiculoUseCase cadastrarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public AtualizarVeiculoUseCase atualizarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public ConsultarVeiculoUseCase consultarVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public ConsultarTodosVeiculosUseCase consultarTodosVeiculosUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public ExcluirVeiculoUseCase excluirVeiculoUseCase(VeiculoRepositoryPort veiculoRepositoryPort) {
        return new VeiculoApplicationService(veiculoRepositoryPort);
    }

    @Bean
    public VeiculoRepositoryPort veiculoRepositoryPort(SpringDataVeiculoRepository springDataRepository) {
        return new VeiculoPersistenceAdapter(springDataRepository);
    }
}