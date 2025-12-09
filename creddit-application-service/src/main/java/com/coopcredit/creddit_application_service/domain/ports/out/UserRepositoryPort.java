package com.coopcredit.creddit_application_service.domain.ports.out;

import com.coopcredit.creddit_application_service.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(Long id);
}
