package com.fitness.activity_service.exceptions;

import com.fitness.activity_service.dtos.APIResponse;
import com.fitness.activity_service.exceptions.custom.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIResponse<Void>> handleBadRequest(BadRequestException ex) {
        APIResponse<Void> response = APIResponse.<Void>builder()
                .success(false)
                .body(null)
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
