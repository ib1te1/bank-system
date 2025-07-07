package ru.troshin.calculator.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.calculator.exception.PrescoringRejectedException;
import ru.troshin.calculator.service.PrescoringService;

import java.time.LocalDate;

@Service
@Slf4j
public class PrescoringServiceImpl implements PrescoringService {

    @Override
    public void validatePassportIssueDate(LocalDate issueDate) {
        log.debug("Prescoring validation for {}", issueDate);
        if (issueDate.isAfter(LocalDate.now())) {
            throw new PrescoringRejectedException("Дата выдачи паспорта не может быть позднее чем текущие дата и время");
        }
    }

}
