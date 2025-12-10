package com.coopcredit.creddit_application_service.application.usecases.auth;

import com.coopcredit.creddit_application_service.domain.exception.NotFoundException;
import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.out.UserRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticateUserUseCaseImpl useCase;

    @Test
    void whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.execute("john", "pass"));
    }

    @Test
    void whenPasswordDoesNotMatch_thenThrowBadCredentials() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("ENC");
        u.setEnabled(true);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> useCase.execute("john", "raw"));
    }

    @Test
    void whenUserDisabled_thenThrowBadCredentials() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("ENC");
        u.setEnabled(false);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> useCase.execute("john", "raw"));
    }

    @Test
    void whenValidCredentials_thenReturnJwtToken() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("ENC");
        u.setRole("ADMIN");
        u.setAffiliateId(null);
        u.setEnabled(true);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("raw", "ENC")).thenReturn(true);
        when(jwtService.generateToken("john", "ADMIN", null)).thenReturn("jwt-token");

        String token = useCase.execute("john", "raw");

        assertEquals("jwt-token", token);
        verify(jwtService).generateToken("john", "ADMIN", null);
    }
}
