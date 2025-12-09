package com.coopcredit.creddit_application_service.application.usecases.auth;

import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.AuthenticateUserUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.UserRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateUserUseCaseImpl(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String execute(String username, String password) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", "username", username));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new BadCredentialsException("User account is disabled");
        }

        // Generate and return JWT token
        return jwtService.generateToken(username, user.getRole(), user.getAffiliateId());
    }
}
