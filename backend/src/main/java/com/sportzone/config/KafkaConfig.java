package com.sportzone.config;

import com.sportzone.event.PedidoCriadoEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.EnableKafka;
import lombok.extern.slf4j.Slf4j;

// Configuração centralizada do Kafka — Producer, Consumer e Tópicos
@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Nome do tópico de pedidos criados
    public static final String TOPICO_PEDIDOS = "sportzone-pedidos";

    // Cria o tópico automaticamente ao iniciar a aplicação
    @Bean
    public NewTopic topicoPedidos() {
        return TopicBuilder.name(TOPICO_PEDIDOS)
                .partitions(3)     // 3 partições para paralelismo
                .replicas(1)       // 1 réplica (ambiente dev)
                .build();
    }

    // ── Producer Configuration ──

    // Factory do Producer para enviar eventos JSON
    @Bean
    public ProducerFactory<String, PedidoCriadoEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    // KafkaTemplate tipado para envio de PedidoCriadoEvent
    @Bean
    public KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ── Consumer Configuration ──

    // Factory do Consumer para receber eventos JSON
    @Bean
    public ConsumerFactory<String, PedidoCriadoEvent> consumerFactory(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sportzone-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<PedidoCriadoEvent> jsonDeserializer = new JsonDeserializer<>(PedidoCriadoEvent.class, objectMapper, false);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    // Factory do Listener container para o @KafkaListener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoCriadoEvent> kafkaListenerContainerFactory(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        ConcurrentKafkaListenerContainerFactory<String, PedidoCriadoEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(objectMapper));

        // Error handler que loga erros de deserialização visivelmente
        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA-ERROR] Falha ao processar mensagem do tópico {}: {}",
                    record.topic(), exception.getMessage(), exception);
        }, new FixedBackOff(1000L, 2)));

        return factory;
    }
}
