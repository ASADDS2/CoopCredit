package com.coopcredit.creddit_application_service.application.usecases.auth;

import com.coopcredit.creddit_application_service.domain.exception.ConflictException;
import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCaseImpl useCase;

    @Test
    void whenUsernameExists_thenThrowConflictException() {
        User input = new User();
        input.setUsername("existing");
        input.setPassword("raw");
        input.setRole("ADMIN");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(input));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenValidUser_thenEncodePassword_SetDefaults_AndSave() {
        User input = new User();
        input.setUsername("newuser");
        input.setPassword("raw");
        input.setRole("ADMIN");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = useCase.execute(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ENCODED", result.getPassword());
        assertTrue(result.isEnabled());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("ENCODED", saved.getPassword());
        assertTrue(saved.isEnabled());
    }
}
