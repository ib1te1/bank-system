package ru.troshin.statement.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.troshin.statement.dto.ClientError;

import java.util.List;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ClientError> handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid json: {}", ex.getMessage());
        return buildClientError("INVALID_REQUEST",
                "Ошибка чтения данных. Проверьте корректность формата и значений полей.");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ClientError> handleHttpClientError(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        log.warn("Ошибка от внешнего сервиса: {}", responseBody);
        try {
            ClientError err = new ObjectMapper().readValue(responseBody, ClientError.class);
            return ResponseEntity.status(ex.getStatusCode()).body(err);
        } catch (Exception parseEx) {
            log.error("Ошибка при разборе ответа внешнего сервиса", parseEx);
            return buildClientError("UNEXPECTED_ERROR", "Произошла неизвестная ошибка");
        }
    }

    private ResponseEntity<ClientError> buildClientError(String code, String message) {
        ClientError err = new ClientError();
        err.setCode(code);
        err.setMessage(message);
        return ResponseEntity.status(400).body(err);
    }

}
