package ru.troshin.calculator.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.calculator.dto.LoanStatementRequestDto;
import ru.troshin.calculator.service.PrescoringService;

import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
public class PrescoringServiceImpl implements PrescoringService {

    @Override
    public void validateBirthdate(LocalDate birthdate) {
        log.debug("Prescoring validation for {}", birthdate);
        if (birthdate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
        int age= Period.between(birthdate,LocalDate.now()).getYears();
        if(age<18){
            log.warn("Prescoring failed: age {} < 18", age);
            throw new IllegalArgumentException("Клиент должен быть старше 18 лет");
        }
    }

    @Override
    public void validatePassportIssueDate(LocalDate issueDate) {
        log.debug("Prescoring validation for {}", issueDate);
        if(issueDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Дата выдачи паспорта не может быть позднее чем текущие дата и время");
        }
    }

}
