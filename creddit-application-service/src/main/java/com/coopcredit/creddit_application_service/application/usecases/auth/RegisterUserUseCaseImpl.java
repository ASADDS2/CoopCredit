package com.coopcredit.creddit_application_service.application.usecases.auth;

import com.coopcredit.creddit_application_service.domain.exception.ConflictException;
import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.in.auth.RegisterUserUseCase;
import com.coopcredit.creddit_application_service.domain.ports.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(User user) {
        // Validation: username must be unique
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("User", "username", user.getUsername());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setEnabled(true);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Save and return
        return userRepository.save(user);
    }
}
