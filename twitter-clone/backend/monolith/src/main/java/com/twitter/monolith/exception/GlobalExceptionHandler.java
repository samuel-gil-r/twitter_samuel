package com.twitter.monolith.exception;

import com.twitter.monolith.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ApiErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(message)
                .timestamp(Instant.now())
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build());
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(UsernameAlreadyTakenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse.builder()
                .status(409)
                .error("Conflict")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiErrorResponse.builder()
                .status(403)
                .error("Forbidden")
                .message("Access denied")
                .timestamp(Instant.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.builder()
                .status(500)
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .timestamp(Instant.now())
                .build());
    }
}
