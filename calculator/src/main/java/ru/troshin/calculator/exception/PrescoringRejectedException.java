package ru.troshin.calculator.exception;

public class PrescoringRejectedException extends CalculatorException {

    public PrescoringRejectedException(String code) {
        super("PRESCORING_REJECTED", code);
    }
}
