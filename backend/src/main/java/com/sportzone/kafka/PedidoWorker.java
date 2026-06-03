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

// Worker assíncrono que consome eventos do Kafka e processa pedidos em background
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoWorker {

    private final PedidoService pedidoService;
    private final SimpMessagingTemplate messagingTemplate;

    // Escuta o tópico e processa cada evento de pedido criado
    @KafkaListener(topics = KafkaConfig.TOPICO_PEDIDOS, groupId = "sportzone-group")
    public void processarPedido(PedidoCriadoEvent evento) {
        log.info("[WORKER] Evento recebido — Pedido: {}", evento.pedidoId());

        try {
            // 1. Processar pagamento via Strategy e avançar status
            log.info("[WORKER] Processando pagamento...");
            pedidoService.processarPagamento(evento);
            notificarFrontend(evento.pedidoId(), "SEPARANDO_ESTOQUE");
            simularProcessamento(2000); // Simula tempo de processamento do gateway

            // 2. Avançar para ENVIADO
            log.info("[WORKER] Despachando pedido...");
            pedidoService.avancarStatus(evento.pedidoId());
            notificarFrontend(evento.pedidoId(), "ENVIADO");
            simularProcessamento(3000); // Simula tempo de despacho

            // 3. Avançar para ENTREGUE
            log.info("[WORKER] Confirmando entrega...");
            pedidoService.avancarStatus(evento.pedidoId());
            notificarFrontend(evento.pedidoId(), "ENTREGUE");

            log.info("[WORKER] Pedido {} processado com sucesso — Status final: ENTREGUE", evento.pedidoId());

        } catch (Exception e) {
            log.error("[WORKER] Erro ao processar pedido {}: {}", evento.pedidoId(), e.getMessage(), e);
        }
    }

    // Envia atualização de status via WebSocket STOMP para o frontend
    private void notificarFrontend(UUID pedidoId, String status) {
        String destino = "/topic/pedidos/" + pedidoId;
        Map<String, String> payload = Map.of(
                "pedidoId", pedidoId.toString(),
                "status", status
        );
        messagingTemplate.convertAndSend(destino, payload);
        log.info("[WORKER] WebSocket enviado — {} → {}", destino, status);
    }

    // Simula tempo de processamento assíncrono (em produção, seriam chamadas reais)
    private void simularProcessamento(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[WORKER] Processamento interrompido");
        }
    }
}
