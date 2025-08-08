package ru.troshin.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.troshin.deal.dto.EmailMessage;
import ru.troshin.deal.dto.Theme;
import ru.troshin.deal.service.EmailProducerService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducerServiceImpl implements EmailProducerService {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    @Override
    public void send(EmailMessage emailMessage) {
        Theme theme=emailMessage.getTheme();
        String topic = theme.toString().toLowerCase().replace('_','-');
        log.info("Sending EmailMessage to topic='{}': {}", topic, emailMessage);

        CompletableFuture<SendResult<String, EmailMessage>> future =
                kafkaTemplate.send(topic, emailMessage);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic='{}': {}", topic, ex.getMessage(), ex);
            } else {
                log.info("Message sent successfully to topic='{}', partition={}, offset={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
