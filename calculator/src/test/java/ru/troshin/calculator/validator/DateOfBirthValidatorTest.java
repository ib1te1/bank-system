package ru.troshin.calculator.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.troshin.calculator.validation.DateOfBirth;
import ru.troshin.calculator.validation.DateOfBirthValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class DateOfBirthValidatorTest {

    private DateOfBirthValidator validator;

    @Mock
    private DateOfBirth annotation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(annotation.minAge()).thenReturn(18);
        when(annotation.maxAge()).thenReturn(120);

        validator = new DateOfBirthValidator();
        validator.initialize(annotation);
    }

    @Test
    void nullIsInvalid() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    void futureDateIsInvalid() {
        LocalDate future = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(future, null));
    }

    @Test
    void tooYoungIsInvalid() {
        LocalDate under18 = LocalDate.now().minusYears(17);
        assertFalse(validator.isValid(under18, null));
    }

    @Test
    void exactly18IsValid() {
        LocalDate exactly18 = LocalDate.now().minusYears(18);
        assertTrue(validator.isValid(exactly18, null));
    }

    @Test
    void tooOldIsInvalid() {
        LocalDate over120 = LocalDate.now().minusYears(121);
        assertFalse(validator.isValid(over120, null));
    }
}

