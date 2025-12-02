package com.rehancode.chatapp.Exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AllFieldsRequired.class)
    public ResponseEntity<ApiResponse<Object>> handleAllFieldsRequired(AllFieldsRequired ex) {

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

        @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> UsernameNotFoundException(UsernameNotFoundException ex) {

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> InvalidCredentialsException(InvalidCredentialsException ex) {

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
