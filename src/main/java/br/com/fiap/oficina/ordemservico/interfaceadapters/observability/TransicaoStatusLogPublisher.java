package br.com.fiap.oficina.ordemservico.interfaceadapters.observability;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.TransicaoStatusOrdemServico;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;

/** Converts a transition into structured logs, without database or transaction access. */
public class TransicaoStatusLogPublisher implements Consumer<TransicaoStatusOrdemServico> {
    @Override
    public void accept(TransicaoStatusOrdemServico transicao) {
        var log = LoggerFactory.getLogger(TransicaoStatusLogPublisher.class);
        log.atInfo().addKeyValue("event", transicao.anterior() == null ? "os.created" : "os.status_changed")
                .addKeyValue("ordem_id", transicao.ordemId()).addKeyValue("os_status", transicao.atual().name())
                .log("Ordem de servico persistida");
        if (transicao.anterior() != null && transicao.duracaoAnteriorMs() != null) {
            log.atInfo().addKeyValue("event", "os.status_duration")
                    .addKeyValue("ordem_id", transicao.ordemId()).addKeyValue("os_status", transicao.anterior().name())
                    .addKeyValue("duracao_ms", transicao.duracaoAnteriorMs())
                    .log("Tempo de permanencia no status concluido");
        }
    }
}
