package ru.troshin.calculator.service;


import java.time.LocalDate;

public interface PrescoringService {

    void validatePassportIssueDate(LocalDate issueDate);
}
