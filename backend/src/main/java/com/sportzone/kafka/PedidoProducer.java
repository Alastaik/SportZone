package com.sportzone.kafka;

import com.sportzone.config.KafkaConfig;
import com.sportzone.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Producer que publica eventos de pedido no tópico Kafka
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoProducer {

    private final KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate;

    // Envia o evento para o tópico usando o pedidoId como chave (garante ordenação por pedido)
    public void enviar(PedidoCriadoEvent evento) {
        log.info("[PRODUCER] Enviando evento para o Kafka — Pedido: {}", evento.pedidoId());

        kafkaTemplate.send(
                KafkaConfig.TOPICO_PEDIDOS,
                evento.pedidoId().toString(),  // Chave: garante que o mesmo pedido vai para a mesma partição
                evento
        );

        log.info("[PRODUCER] Evento enviado com sucesso para o tópico '{}'", KafkaConfig.TOPICO_PEDIDOS);
    }
}
