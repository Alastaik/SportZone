package com.sportzone.kafka;

import com.sportzone.config.KafkaConfig;
import com.sportzone.event.PedidoCriadoEvent;
import com.sportzone.model.enums.MetodoPagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoProducerTest {

    @Mock
    private KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate;

    @InjectMocks
    private PedidoProducer pedidoProducer;

    @Test
    void enviar_DeveEnviarEventoCorretamenteParaOTopico() {
        UUID pedidoId = UUID.randomUUID();
        PedidoCriadoEvent evento = new PedidoCriadoEvent(pedidoId, new BigDecimal("100.00"), MetodoPagamento.PIX);

        pedidoProducer.enviar(evento);

        verify(kafkaTemplate).send(KafkaConfig.TOPICO_PEDIDOS, pedidoId.toString(), evento);
    }
}
