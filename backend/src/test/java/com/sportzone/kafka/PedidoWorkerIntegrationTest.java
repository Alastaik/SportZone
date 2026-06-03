package com.sportzone.kafka;

import com.sportzone.event.PedidoCriadoEvent;
import com.sportzone.service.PedidoService;
import com.sportzone.model.enums.MetodoPagamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"sportzone-pedidos"})
class PedidoWorkerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate;

    // Mockamos os serviços pesados para isolar apenas o teste do fluxo Kafka
    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void processarPedido_DeveConsumirMensagemEAvancarStatus() {
        // Preparação
        UUID pedidoId = UUID.randomUUID();
        PedidoCriadoEvent event = new PedidoCriadoEvent(pedidoId, new BigDecimal("150.00"), MetodoPagamento.CARTAO_CREDITO);

        // Execução: Produtor envia evento para o Kafka (Embedded)
        kafkaTemplate.send("sportzone-pedidos", pedidoId.toString(), event);

        // Validação: Verificamos se o Consumer (Worker) leu a mensagem e chamou os métodos esperados.
        // O Worker tem Thread.sleep() simulando tempo, então usamos timeout de 6 segundos.
        
        // 1. Deve chamar processarPagamento
        verify(pedidoService, timeout(6000)).processarPagamento(any(PedidoCriadoEvent.class));
        
        // 2. Deve avançar o status duas vezes (ENVIADO e ENTREGUE)
        verify(pedidoService, timeout(6000).times(2)).avancarStatus(eq(pedidoId));
    }
}
