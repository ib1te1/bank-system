package ru.troshin.dossier.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @PostConstruct
    public void logCredentials() {
        if (mailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
            log.debug("Mail credentials: host='{}', port={}, username='{}', password='{}'",
                    impl.getHost(), impl.getPort(), impl.getUsername(), impl.getPassword());
        }
    }

    public void send(String to, String subject, String text) {
        log.debug("Sending from JavaMailSender: to='{}', subject='{}'", to, subject);
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        try {
            mailSender.send(mail);
            log.info("JavaMailSender успешно отправил письмо to='{}'", to);
        }
        catch (MailSendException ex) {
            log.error("Не удалось отправить письмо to='{}': {}", to, ex.getMessage());
            Throwable cause = ex.getCause();
            while (cause != null) {
                log.error("Caused by: {} — {}", cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
            }
            throw ex;
        }
        catch (Exception ex) {
            log.error("JavaMailSender ошибка при отправке to='{}': {}", to, ex.getMessage(), ex);
            throw ex;
        }
    }
}
