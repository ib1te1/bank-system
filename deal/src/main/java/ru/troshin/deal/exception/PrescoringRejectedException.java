package ru.troshin.deal.exception;

public class PrescoringRejectedException extends CalculatorException {

    public PrescoringRejectedException(String code) {
        super("PRESCORING_REJECTED", code);
    }
}
