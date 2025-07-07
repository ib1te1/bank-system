package ru.troshin.calculator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOfBirthValidator.class)
public @interface DateOfBirth {
    String message() default "Некорректная дата рождения";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


    int minAge() default 18;

    int maxAge() default 120;
}
