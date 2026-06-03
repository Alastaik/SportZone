package com.sportzone.kafka;

import com.sportzone.config.KafkaConfig;
import com.sportzone.event.PedidoCriadoEvent;
import com.sportzone.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoWorker {

    private final PedidoService pedidoService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = KafkaConfig.TOPICO_PEDIDOS, groupId = "sportzone-group")
    public void processarPedido(PedidoCriadoEvent evento) {
        log.info("[WORKER] Evento recebido — Pedido: {}", evento.pedidoId());

        try {
            // Pagamento via Strategy
            log.info("[WORKER] Processando pagamento...");
            pedidoService.processarPagamento(evento);
            notificarFrontend(evento.pedidoId(), "SEPARANDO_ESTOQUE");
            simularProcessamento(2000); 

            // Despacho
            log.info("[WORKER] Despachando pedido...");
            pedidoService.avancarStatus(evento.pedidoId());
            notificarFrontend(evento.pedidoId(), "ENVIADO");
            simularProcessamento(3000); 

            // Entrega
            log.info("[WORKER] Confirmando entrega...");
            pedidoService.avancarStatus(evento.pedidoId());
            notificarFrontend(evento.pedidoId(), "ENTREGUE");

            log.info("[WORKER] Pedido {} processado com sucesso", evento.pedidoId());

        } catch (Exception e) {
            log.error("[WORKER] Erro ao processar pedido {}: {}", evento.pedidoId(), e.getMessage(), e);
        }
    }

    // Broadcast WebSocket STOMP
    private void notificarFrontend(UUID pedidoId, String status) {
        String destino = "/topic/pedidos/" + pedidoId;
        Map<String, String> payload = Map.of(
                "pedidoId", pedidoId.toString(),
                "status", status
        );
        messagingTemplate.convertAndSend(destino, payload);
        log.info("[WORKER] WebSocket enviado — {} → {}", destino, status);
    }

    private void simularProcessamento(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[WORKER] Processamento interrompido");
        }
    }
}
