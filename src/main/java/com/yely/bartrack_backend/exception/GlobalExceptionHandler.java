package com.yely.bartrack_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yely.bartrack_backend.domain.ConflictException;
import com.yely.bartrack_backend.domain.ForbiddenOperationException;
import com.yely.bartrack_backend.domain.ResourceNotFoundException;
import com.yely.bartrack_backend.domain.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice

public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status, String path) {
        return new ResponseEntity<>(new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()), status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, req.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, req.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, req.getRequestURI());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex, HttpServletRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest req) {
        return buildResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildResponse(
                message,
                HttpStatus.BAD_REQUEST,
                req.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest req) {

        return buildResponse(
                "Malformed JSON request",
                HttpStatus.BAD_REQUEST,
                req.getRequestURI());
    }
}