package br.com.fiap.oficina.ordemservico.config;

import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.OrdemServicoPersistenceAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.SpringDataOrdemServicoRepository;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.service.OrdemServicoApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemServicoConfiguration {
    @Bean
    public CriarOrdemServicoUseCase criarOrdemServicoUseCase(OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        return new OrdemServicoApplicationService(ordemServicoRepositoryPort);
    }

    @Bean
    public ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase(OrdemServicoRepositoryPort ordemServicoRepositoryPort) {
        return new OrdemServicoApplicationService(ordemServicoRepositoryPort);
    }

    @Bean
    public OrdemServicoRepositoryPort ordemServicoRepositoryPort(SpringDataOrdemServicoRepository springDataRepository) {
        return new OrdemServicoPersistenceAdapter(springDataRepository);
    }
}