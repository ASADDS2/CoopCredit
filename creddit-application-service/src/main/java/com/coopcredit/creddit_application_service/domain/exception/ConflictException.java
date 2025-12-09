package com.coopcredit.creddit_application_service.domain.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resource, String field, String value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}
