package ru.troshin.deal.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.troshin.deal.dto.ClientError;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchStatementException.class)
    public ResponseEntity<ClientError> handleNoSuchStatement(NoSuchStatementException ex) {
        log.warn("No such statement in db: {}", ex.getMessage());
        return buildClientError("STATEMENT_NOT_FOUND", ex.getMessage());
    }

    private ResponseEntity<ClientError> buildClientError(String code, String message) {
        ClientError err = new ClientError();
        err.setCode(code);
        err.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}
