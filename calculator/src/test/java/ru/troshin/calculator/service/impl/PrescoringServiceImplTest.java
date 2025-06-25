package ru.troshin.calculator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.troshin.calculator.service.PrescoringService;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class PrescoringServiceImplTest {

    private PrescoringService prescoringService;
    private static final ZoneId ZONE = ZoneId.of("Europe/Saratov");

    @BeforeEach
    void setUp() {
        prescoringService = new PrescoringServiceImpl();
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
