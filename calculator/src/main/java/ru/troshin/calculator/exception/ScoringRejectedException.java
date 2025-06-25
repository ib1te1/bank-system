package ru.troshin.calculator.exception;

public class ScoringRejectedException extends CalculatorException {
    public ScoringRejectedException(String message) {
        super("SCORING_REJECTED", message);
    }
}
