package br.com.fiap.oficina.ordemservico.adapter.in.event;

import br.com.fiap.oficina.ordemservico.domain.event.FaltaPecaEstoqueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GestorNotificacaoListener {

    private static final Logger log = LoggerFactory.getLogger(GestorNotificacaoListener.class);

    @EventListener
    public void onFaltaPecaEstoque(FaltaPecaEstoqueEvent event) {
        log.info("[NOTIFICACAO] Falta de peca no estoque. Ordem: {}, ItemEstoque: {}, Quantidade solicitada: {}",
                event.ordemServicoId(), event.itemEstoqueId(), event.quantidadeSolicitada());
    }
}