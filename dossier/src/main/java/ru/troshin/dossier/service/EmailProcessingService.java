package ru.troshin.dossier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.dossier.dto.EmailMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessingService {
    private final MailService mailService;

    public void handleEmail(EmailMessage msg) {
        String subject = "[Credit Dossier] " + msg.getTheme();
        log.debug("Подготовка e-mail: to='{}', subject='{}', text='{}…'",
                msg.getAddress(), subject,
                msg.getText().length() > 50 ? msg.getText().substring(0, 50) + "…" : msg.getText());
        try {
            mailService.send(msg.getAddress(), subject, msg.getText());
            log.info("MailService отправил письмо [to='{}', statementId='{}']",
                    msg.getAddress(), msg.getStatementId());
        } catch (Exception ex) {
            log.error("MailService не смог отправить письмо [to='{}', statementId='{}']: {}",
                    msg.getAddress(), msg.getStatementId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}