package ru.troshin.gateway.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GatewayExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClient(RestClientException ex, HttpServletRequest req) {
        String service = resolveService(req);
        log.error("RestClientException when calling downstream service={}", service, ex);
        ErrorResponse body = new ErrorResponse();
        body.setStatus(HttpStatus.BAD_GATEWAY.value());
        body.setError(HttpStatus.BAD_GATEWAY.getReasonPhrase());
        body.setMessage("Error contacting downstream service");
        body.setPath(req.getRequestURI());
        body.setDownstreamService(service);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleRestClientResponse(RestClientResponseException ex, HttpServletRequest req) {
        String service = resolveService(req);
        int downstreamStatus = ex.getStatusCode().value();

        byte[] rawBytes = ex.getResponseBodyAsByteArray();
        String decoded = decodeResponseBody(rawBytes, ex.getResponseHeaders());

        log.error("Downstream {} returned status={} bytes={}", service, downstreamStatus, rawBytes.length);
        log.debug("Downstream decoded body: {}", decoded);

        String readable = extractReadableDownstreamMessageSimple(decoded);

        HttpStatus status = HttpStatus.resolve(downstreamStatus);

        ErrorResponse body = new ErrorResponse();
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage("Downstream returned error");
        body.setPath(req.getRequestURI());
        body.setDownstreamService(service);
        body.setDownstreamStatus(downstreamStatus);
        body.setDownstreamMessage(readable);
        body.setDownstreamBody(decoded);

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<ErrorResponse> handleDownstream(DownstreamException ex, HttpServletRequest req) {
        log.error("Downstream error from service={}, status={}, path={}",
                ex.getServiceName(), ex.getDownstreamStatus(), req.getRequestURI(), ex);

        HttpStatus status = HttpStatus.BAD_GATEWAY;
        if (ex.getDownstreamStatus() != null) {
            HttpStatus resolved = HttpStatus.resolve(ex.getDownstreamStatus());
            if (resolved != null) {
                status = resolved;
            }
        }
        ErrorResponse body = new ErrorResponse();
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(ex.getMessage());
        body.setPath(req.getRequestURI());
        body.setDownstreamService(ex.getServiceName());
        body.setDownstreamStatus(ex.getDownstreamStatus());
        body.setDownstreamBody(ex.getDownstreamBody());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse body = new ErrorResponse();
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        body.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.setMessage("Validation failed: " + errors);
        body.setPath(req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse();
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        body.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.setMessage("Malformed request body: " + ex.getMessage());
        body.setPath(req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error for request " + req.getRequestURI(), ex);

        ErrorResponse body = new ErrorResponse();
        body.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.setMessage("Internal server error");
        body.setPath(req.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String resolveService(HttpServletRequest req) {
        Object attr = req.getAttribute("downstreamService");
        return attr == null ? "unknown" : String.valueOf(attr);
    }

    private String decodeResponseBody(byte[] bytes, HttpHeaders headers) {
        if (bytes == null || bytes.length == 0) return null;

        if (headers != null && headers.getContentType() != null && headers.getContentType().getCharset() != null) {
            Charset charset = headers.getContentType().getCharset();
            return new String(bytes, charset);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String extractReadableDownstreamMessageSimple(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            JsonNode root = objectMapper.readTree(raw);
            if (root.has("message")) {
                String msg = root.get("message").asText(null);
                if (root.has("code")) {
                    String code = root.get("code").asText(null);
                    return code == null ? msg : code + ": " + msg;
                }
                return msg;
            }
            if (root.has("detail")) return root.get("detail").asText(null);
            if (root.has("title")) {
                String title = root.get("title").asText(null);
                String detail = root.has("detail") ? root.get("detail").asText(null) : null;
                return detail == null ? title : title + ": " + detail;
            }
            if (root.has("errors") && root.get("errors").isArray() && !root.get("errors").isEmpty()) {
                JsonNode first = root.get("errors").get(0);
                if (first.has("message")) return first.get("message").asText(null);
                if (first.isTextual()) return first.asText(null);
            }
            return raw;
        } catch (Exception e) {
            log.debug("Failed to parse downstream body as JSON, returning raw. Reason: {}", e.toString());
            return raw;
        }
    }

}
