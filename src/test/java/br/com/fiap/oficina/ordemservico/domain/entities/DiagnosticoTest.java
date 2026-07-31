package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DiagnosticoTest {

    @Test
    void registrarCriaDiagnosticoValido() {
        var diagnostico = Diagnostico.registrar("Pastilhas desgastadas");

        assertNotNull(diagnostico.id());
        assertEquals("Pastilhas desgastadas", diagnostico.descricao());
        assertNotNull(diagnostico.criadoEm());
        assertNotNull(diagnostico.atualizadoEm());
    }

    @Test
    void construtorRejeitaIdNulo() {
        assertThrows(DomainException.class,
                () -> new Diagnostico(null, "Descricao", OffsetDateTime.now(), OffsetDateTime.now()));
    }

    @Test
    void construtorRejeitaDescricaoEmBranco() {
        assertThrows(DomainException.class,
                () -> new Diagnostico(UUID.randomUUID(), " ", OffsetDateTime.now(), OffsetDateTime.now()));
    }

    @Test
    void atualizarDescricaoAlteraTextoEData() {
        var diagnostico = Diagnostico.registrar("Descricao inicial");
        var atualizadoEm = diagnostico.atualizadoEm();

        diagnostico.atualizarDescricao("Descricao revisada");

        assertEquals("Descricao revisada", diagnostico.descricao());
        assertTrue(diagnostico.atualizadoEm().isAfter(atualizadoEm) || diagnostico.atualizadoEm().isEqual(atualizadoEm));
    }
}
