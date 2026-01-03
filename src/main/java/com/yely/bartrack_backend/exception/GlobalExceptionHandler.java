package com.yely.bartrack_backend.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yely.bartrack_backend.domain.ConflictException;
import com.yely.bartrack_backend.domain.ForbiddenOperationException;
import com.yely.bartrack_backend.domain.ResourceNotFoundException;
import com.yely.bartrack_backend.domain.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice

public class GlobalExceptionHandler {
        private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                        String message,
                        HttpServletRequest request) {
                return ResponseEntity.status(status)
                                .body(new ErrorResponse(status.value(),
                                                status.getReasonPhrase(),
                                                message,
                                                request.getRequestURI(),
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                String message = exception.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .collect(Collectors.joining("; "));
                return buildResponse(HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation failed" : message,
                                request);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleInvalidJson(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest req) {

                return buildResponse(
                                HttpStatus.BAD_REQUEST,
                                "Malformed JSON request",
                                req);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        ValidationException ex,
                        HttpServletRequest req) {

                return buildResponse(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                req);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest req) {

                return buildResponse(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage(),
                                req);
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponse> handleConflict(
                        ConflictException ex,
                        HttpServletRequest req) {

                return buildResponse(
                                HttpStatus.CONFLICT,
                                ex.getMessage(),
                                req);
        }

        @ExceptionHandler(ForbiddenOperationException.class)
        public ResponseEntity<ErrorResponse> handleForbidden(
                        ForbiddenOperationException ex,
                        HttpServletRequest req) {

                return buildResponse(
                                HttpStatus.FORBIDDEN,
                                ex.getMessage(),
                                req);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(
                        Exception ex,
                        HttpServletRequest req) {

                log.error("Unhandled exception occurred at {}: ", req.getRequestURI(), ex);

                return buildResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Internal Server Error. Please contact support.",
                                req);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                        AccessDeniedException ex,
                        HttpServletRequest req) {

                log.warn("Access denied for URI {}: {}", req.getRequestURI(), ex.getMessage());

                return buildResponse(
                                HttpStatus.FORBIDDEN,
                                "Access Denied: You do not have permission to perform this action",
                                req);
        }
}
