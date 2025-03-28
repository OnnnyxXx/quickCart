package com.quickcart.quickCart.store.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String size;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            HttpServletRequest request) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", "Максимальный размер файла логотипа составляет %s.".formatted(size));
        return ResponseEntity.status(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<Map<String, String>> forbiddenEx(
            HttpClientErrorException.Forbidden exc,
            HttpServletRequest request) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Доступ запрещён");
        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(errorResponse);
    }

}
