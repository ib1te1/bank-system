package ru.troshin.calculator.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirth, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(DateOfBirth annotation) {
        this.minAge = annotation.minAge();
        this.maxAge = annotation.maxAge();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if(localDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (localDate.isAfter(today)) {
            return false;
        }
        int age = Period.between(localDate, today).getYears();
        return age >= minAge && age <= maxAge;
    }
}
