package br.com.fiap.oficina.servico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.DomainException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ServicoTest {
    @Test
    void criarNormalizaCodigoEPreencheDatas() {
        var servico = Servico.criar(" troca ", "Troca de oleo", BigDecimal.TEN, 60);

        assertEquals("TROCA", servico.codigo());
        assertTrue(servico.ativo());
        assertNotNull(servico.criadoEm());
        assertNotNull(servico.atualizadoEm());
    }

    @Test
    void atualizarAlteraDadosPermitidos() {
        var servico = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);

        servico.atualizar("Alinhamento", BigDecimal.valueOf(120), 90);

        assertEquals("Alinhamento", servico.descricao());
        assertEquals(BigDecimal.valueOf(120), servico.valorUnitario());
        assertEquals(90, servico.tempoEstimadoMinutos());
    }

    @Test
    void rejeitaValorNegativo() {
        assertThrows(DomainException.class, () -> Servico.criar("TROCA", "Troca", BigDecimal.valueOf(-1), 60));
    }
}