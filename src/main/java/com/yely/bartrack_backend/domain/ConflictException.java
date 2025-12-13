package com.yely.bartrack_backend.domain;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message);
    }
}