package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.event;

import br.com.fiap.oficina.ordemservico.domain.events.OrcamentoFechadoEvent;
import br.com.fiap.oficina.ordemservico.domain.events.OrdemServicoFinalizadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteNotificacaoListener {

    private static final Logger log = LoggerFactory.getLogger(ClienteNotificacaoListener.class);

    @EventListener
    public void onOrcamentoFechado(OrcamentoFechadoEvent event) {
        log.info("[NOTIFICACAO] Orcamento enviado ao cliente {} para aprovacao. Ordem: {}",
                event.clienteId(), event.ordemServicoId());
    }

    @EventListener
    public void onOrdemFinalizada(OrdemServicoFinalizadaEvent event) {
        log.info("[NOTIFICACAO] Veiculo pronto para retirada. Cliente: {}, Ordem: {}",
                event.clienteId(), event.ordemServicoId());
    }
}
