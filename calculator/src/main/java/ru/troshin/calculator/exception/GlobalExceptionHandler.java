package ru.troshin.calculator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.troshin.calculator.dto.ClientError;
import ru.troshin.calculator.dto.UnexpectedError;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ClientError> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Некорректный запрос";
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        if (!errors.isEmpty()) {
            FieldError first = errors.get(0);
            message = first.getField() + ": " + first.getDefaultMessage();
        }
        return buildClientError("INVALID_REQUEST", message);
    }

    @ExceptionHandler(PrescoringRejectedException.class)
    public ResponseEntity<ClientError> handleInvalidRequest(PrescoringRejectedException ex) {
        log.warn("Prescoring rejected: {}", ex.getMessage());
        return buildClientError(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ScoringRejectedException.class)
    public ResponseEntity<ClientError> handleIllegalArgument(ScoringRejectedException ex) {
        log.warn("Scoring rejected: {}", ex.getMessage());
        return buildClientError(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnexpectedError> handleAll(Exception ex) {
        log.error("Unexpected exception", ex);
        UnexpectedError body = new UnexpectedError();
        body.setCode("UNEXPECTED_ERROR");
        body.setMessage("Произошла неизвестная ошибка");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<ClientError> buildClientError(String code, String message) {
        ClientError err = new ClientError();
        err.setCode(code);
        err.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}
