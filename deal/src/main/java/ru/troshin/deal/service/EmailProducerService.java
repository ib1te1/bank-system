package ru.troshin.deal.service;

import ru.troshin.deal.dto.EmailMessage;

public interface EmailProducerService {

    public void send(EmailMessage emailMessage);
}
