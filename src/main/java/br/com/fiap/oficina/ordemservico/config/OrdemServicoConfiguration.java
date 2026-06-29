package br.com.fiap.oficina.ordemservico.config;

import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.OrdemServicoPersistenceAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.SpringDataOrdemServicoRepository;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemServicoConfiguration {

    @Bean
    public OrdemServicoRepositoryPort ordemServicoRepositoryPort(SpringDataOrdemServicoRepository springDataRepository) {
        return new OrdemServicoPersistenceAdapter(springDataRepository);
    }
}
