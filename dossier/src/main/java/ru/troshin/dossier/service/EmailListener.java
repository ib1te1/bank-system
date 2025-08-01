package ru.troshin.dossier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.troshin.dossier.dto.EmailMessage;
import ru.troshin.dossier.dto.KafkaTopics;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailListener {
    private final EmailProcessingService processingService;

    @KafkaListener(
            topics = {KafkaTopics.FINISH_REGISTRATION,
                    KafkaTopics.CREATE_DOCUMENTS,
                    KafkaTopics.SEND_DOCUMENTS,
                    KafkaTopics.SEND_SES,
                    KafkaTopics.CREDIT_ISSUED,
                    KafkaTopics.STATEMENT_DENIED},
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(
            EmailMessage msg,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        log.info("Получено сообщение из Kafka [topic='{}']: {}", topic, msg);
        try {
            processingService.handleEmail(msg);
            log.info("Успешно обработано сообщение [statementId='{}', theme='{}']",
                    msg.getStatementId(), msg.getTheme());
        } catch (Exception ex) {
            log.error("Ошибка при обработке сообщения [statementId='{}', theme='{}']: {}",
                    msg.getStatementId(), msg.getTheme(), ex.getMessage(), ex);
            throw ex;
        }
    }

}
