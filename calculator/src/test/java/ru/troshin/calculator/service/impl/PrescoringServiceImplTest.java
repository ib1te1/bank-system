package ru.troshin.calculator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.troshin.calculator.service.PrescoringService;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class PrescoringServiceImplTest {

    private PrescoringService prescoringService;
    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    @BeforeEach
    void setUp() {
        prescoringService = new PrescoringServiceImpl();
    }

    @Test
    void validateBirthdate_futureDate_throws() {
        LocalDate future = LocalDate.now(ZONE).plusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                prescoringService.validateBirthdate(future));
        assertTrue(ex.getMessage().contains("не может быть в будущем"));
    }

    @Test
    void validateBirthdate_tooYoung_throws() {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate under18 = today.minusYears(17).plusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                prescoringService.validateBirthdate(under18));
        assertTrue(ex.getMessage().contains("Клиент должен быть старше 18 лет")
                || ex.getMessage().contains("не младше 18"));
    }

    @Test
    void validateBirthdate_exactly18_ok() {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate exactly18 = today.minusYears(18);
        assertDoesNotThrow(() -> prescoringService.validateBirthdate(exactly18));
    }

    @Test
    void validatePassportIssueDate_futureDate_throws() {
        LocalDate future = LocalDate.now(ZONE).plusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                prescoringService.validatePassportIssueDate(future));
        assertTrue(ex.getMessage().contains("не может быть"));
    }

    @Test
    void validatePassportIssueDate_pastDate_ok() {
        LocalDate past = LocalDate.now(ZONE).minusDays(1);
        assertDoesNotThrow(() -> prescoringService.validatePassportIssueDate(past));
    }
}
