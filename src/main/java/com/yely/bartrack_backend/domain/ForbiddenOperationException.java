package com.yely.bartrack_backend.domain;

public class ForbiddenOperationException extends DomainException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
