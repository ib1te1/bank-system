package ru.troshin.deal.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.troshin.deal.calculator.dto.UnexpectedError;
import ru.troshin.deal.dto.ClientError;

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
        log.warn("Invalid JSON: {}", ex.getMessage());
        return buildClientError(
                "INVALID_REQUEST",
                "Ошибка чтения данных. Проверьте корректность формата и значений полей."
        );
    }

    @ExceptionHandler(NoSuchStatementException.class)
    public ResponseEntity<ClientError> handleNoSuchStatement(NoSuchStatementException ex) {
        log.warn("No such statement in db: {}", ex.getMessage());
        return buildClientError("STATEMENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(PrescoringRejectedException.class)
    public ResponseEntity<ClientError> handlePrescoring(PrescoringRejectedException ex) {
        log.warn("Prescoring rejected: {}", ex.getMessage());
        return buildClientError(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ScoringRejectedException.class)
    public ResponseEntity<ClientError> handleScoring(ScoringRejectedException ex) {
        log.warn("Scoring rejected: {}", ex.getMessage());
        return buildClientError(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ClientError> handleHttpClientError(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        log.warn("Ошибка от внешнего сервиса: {}", responseBody);
        try {
            ClientError clientError = new ObjectMapper()
                    .readValue(responseBody, ClientError.class);
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(clientError);
        } catch (Exception parseEx) {
            log.error("Ошибка при разборе ответа внешнего сервиса", parseEx);
            return buildClientError("UNEXPECTED_ERROR", "Произошла неизвестная ошибка");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnexpectedError> handleAll(Exception ex) {
        log.error("Unexpected exception", ex);
        UnexpectedError body = new UnexpectedError();
        body.setCode("UNEXPECTED_ERROR");
        body.setMessage("Произошла неизвестная ошибка");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    private ResponseEntity<ClientError> buildClientError(String code, String message) {
        ClientError err = new ClientError();
        err.setCode(code);
        err.setMessage(message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err);
    }
}