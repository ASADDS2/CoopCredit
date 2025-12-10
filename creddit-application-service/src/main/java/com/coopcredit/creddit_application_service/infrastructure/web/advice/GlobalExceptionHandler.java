package com.coopcredit.creddit_application_service.infrastructure.web.advice;

import com.coopcredit.creddit_application_service.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/not-found"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        problemDetail.setTitle("Resource Conflict");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/conflict"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler({
            BusinessRuleException.class,
            AffiliateNotActiveException.class,
            InsufficientIncomeException.class
    })
    public ProblemDetail handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/business-rule"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields");
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/validation"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
            WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid or unreadable. Please verify the JSON structure.");
        problemDetail.setTitle("Invalid Request Body");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/invalid-request-body"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password");
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/authentication"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");
        problemDetail.setTitle("Access Denied");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/access-denied"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/invalid-argument"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support with the trace ID.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.coopcredit.com/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("traceId", generateTraceId());
        problemDetail.setProperty("errorType", ex.getClass().getSimpleName());
        return problemDetail;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
