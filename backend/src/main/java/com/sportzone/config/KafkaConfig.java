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

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public static final String TOPICO_PEDIDOS = "sportzone-pedidos";

    @Bean
    public NewTopic topicoPedidos() {
        return TopicBuilder.name(TOPICO_PEDIDOS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    // --- Producer ---
    
    @Bean
    public ProducerFactory<String, PedidoCriadoEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // --- Consumer ---

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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoCriadoEvent> kafkaListenerContainerFactory(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        ConcurrentKafkaListenerContainerFactory<String, PedidoCriadoEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(objectMapper));

        // Error handler com retry (2 tentativas, 1s de intervalo)
        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA-ERROR] Falha no tópico {}: {}", record.topic(), exception.getMessage());
        }, new FixedBackOff(1000L, 2)));

        return factory;
    }
}
