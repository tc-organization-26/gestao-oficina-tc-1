package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.ordemservico.domain.model.Diagnostico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrdemServicoResponseTest {

    @Test
    void fromIncluiDiagnosticoQuandoOrdemPossuiDiagnostico() {
        var ordem = OrdemServico.criar(
                new ClienteId(UUID.randomUUID()),
                new VeiculoId(UUID.randomUUID()),
                "Revisao");
        ordem.iniciarDiagnostico();
        ordem.registrarDiagnostico(Diagnostico.registrar("Trocar freios"));

        var response = OrdemServicoResponse.from(ordem);

        assertNotNull(response.diagnostico());
        assertEquals("Trocar freios", response.diagnostico().descricao());
    }

}