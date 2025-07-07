package ru.troshin.calculator.service;

import ru.troshin.calculator.dto.ScoringDataDto;

import java.math.BigDecimal;

public interface ScoringService {

    BigDecimal calculateRate(ScoringDataDto scoringDto, BigDecimal baseRate);
}
