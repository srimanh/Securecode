package com.securecode.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        // Log the error internally (would use SLF4J in a real app)
        System.err.println("INTERNAL ERROR: " + ex.getMessage());

        return new ResponseEntity<>(Map.of(
                "error", "Analysis unavailable",
                "message", "SecureCode encountered an internal error. Our team has been notified."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
