package br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa;

import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.TransicaoStatusOrdemServico;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

/** Database history access only. The V4 trigger persists transitions atomically. */
public class HistoricoStatusOrdemServicoJpa {
    private final EntityManager entityManager;

    public HistoricoStatusOrdemServicoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public long bloquearEObterUltimoRegistro(UUID ordemId) {
        entityManager.createNativeQuery("select id from ordem_servico where id = :id for update")
                .setParameter("id", ordemId).getResultList();
        return ((Number) entityManager.createNativeQuery(
                "select coalesce(max(id), 0) from ordem_servico_status_historico where ordem_id = :id")
                .setParameter("id", ordemId).getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<TransicaoStatusOrdemServico> buscarApos(UUID ordemId, long cursor) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                select status_anterior, status_atual, duracao_anterior_ms
                from ordem_servico_status_historico where ordem_id = :id and id > :cursor order by id
                """, Object[].class).setParameter("id", ordemId).setParameter("cursor", cursor).getResultList();
        return rows.stream().map(row -> new TransicaoStatusOrdemServico(ordemId,
                row[0] == null ? null : StatusOrdemServico.values()[((Number) row[0]).intValue()],
                StatusOrdemServico.values()[((Number) row[1]).intValue()],
                row[2] == null ? null : ((Number) row[2]).longValue())).toList();
    }
}
