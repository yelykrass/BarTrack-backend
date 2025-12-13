package com.yely.bartrack_backend.domain;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
