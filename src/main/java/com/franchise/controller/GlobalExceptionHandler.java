package com.franchise.controller;

import com.franchise.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponseDTO<String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.badRequest()
                .body(ApiResponseDTO.error(ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponseDTO<Map<String, String>>>> handleValidationException(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return Mono.just(ResponseEntity.badRequest()
                .body(ApiResponseDTO.error("Validation failed").getData() != null ?
                        new ApiResponseDTO<>(false, "Validation failed", errors) :
                        ApiResponseDTO.error("Validation failed")));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponseDTO<String>>> handleGenericException(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("An unexpected error occurred: " + ex.getMessage())));
    }
}

