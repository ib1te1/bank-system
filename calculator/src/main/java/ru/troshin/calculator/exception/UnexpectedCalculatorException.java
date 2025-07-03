package ru.troshin.calculator.exception;

public class UnexpectedCalculatorException extends CalculatorException {
    public UnexpectedCalculatorException(String message) {
        super("UNEXPECTED_ERROR", message);
    }
}
