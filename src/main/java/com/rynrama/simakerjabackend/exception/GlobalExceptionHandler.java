package com.rynrama.simakerjabackend.exception;

import com.rynrama.simakerjabackend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            String fieldName = fieldError.getField()
                    .replaceAll("([a-z])([A-Z])", "$1_$2")
                    .toLowerCase();
            error.put("field", fieldName);
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "validation error");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            String fieldName = ife.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .collect(Collectors.joining("."));
            String validValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            Map<String, String> error = new HashMap<>();
            error.put("field", fieldName);
            error.put("message", "invalid value '" + ife.getValue() + "'. Accepted values: [" + validValues + "]");

            response.put("message", "validation error");
            response.put("errors", List.of(error));
        } else {
            response.put("message", "malformed request body");
        }

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
