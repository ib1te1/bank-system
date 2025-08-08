package ru.troshin.dossier.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.troshin.dossier.dto.EmailMessage;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, EmailMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,              groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES,"ru.troshin.dossier.dto");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(EmailMessage.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailMessage>
    kafkaListenerContainerFactory(FixedBackOff fixedBackOff) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, EmailMessage>();
        factory.setConsumerFactory(consumerFactory());
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        errorHandler.setRetryListeners((record, ex, attempt) ->
                log.warn("Retry #{} for record key='{}' from topic='{}' failed: {}",
                        attempt, record.key(), record.topic(), ex.getMessage())
        );
        factory.setCommonErrorHandler(errorHandler);
        log.debug("KafkaListenerContainerFactory configured: {}", factory);
        return factory;
    }
    @Bean
    public FixedBackOff fixedBackOff() {
        log.debug("Creating FixedBackOff with interval=5000ms, maxAttempts=3");
        return new FixedBackOff(5000L, 3L);
    }
}