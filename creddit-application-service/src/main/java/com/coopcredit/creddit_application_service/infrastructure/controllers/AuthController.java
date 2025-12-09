package com.coopcredit.creddit_application_service.infrastructure.controllers;

import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.AuthenticateUserUseCase;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.RegisterUserUseCase;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.auth.AuthResponse;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.auth.LoginRequest;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.auth.RegisterRequest;
import com.coopcredit.creddit_application_service.infrastructure.web.response.AppResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
            AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AppResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        // Map DTO to domain
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setAffiliateId(request.getAffiliateId());

        // Execute use case
        User createdUser = registerUserUseCase.execute(user);

        // Create response
        AuthResponse response = new AuthResponse(
                null, // No token on registration
                createdUser.getUsername(),
                createdUser.getRole(),
                createdUser.getAffiliateId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AppResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<AppResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        // Execute authentication
        String token = authenticateUserUseCase.execute(request.getUsername(), request.getPassword());

        // Create response (you may want to include user details from token)
        AuthResponse response = new AuthResponse(
                token,
                request.getUsername(),
                null, // Extract from token if needed
                null // Extract from token if needed
        );

        return ResponseEntity.ok(AppResponse.success("Login successful", response));
    }
}
