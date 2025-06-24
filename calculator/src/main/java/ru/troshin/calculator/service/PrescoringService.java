package ru.troshin.calculator.service;


import java.time.LocalDate;

public interface PrescoringService {

    void validateBirthdate(LocalDate birthdate);
    void validatePassportIssueDate(LocalDate issueDate);
}
