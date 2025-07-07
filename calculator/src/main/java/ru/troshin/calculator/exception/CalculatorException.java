package ru.troshin.calculator.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculatorException extends RuntimeException {

    private final String code;

    public CalculatorException(String code, String message) {
        super(message);
        this.code = code;
    }

}
