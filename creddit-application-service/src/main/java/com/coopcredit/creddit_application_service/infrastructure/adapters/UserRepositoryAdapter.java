package com.coopcredit.creddit_application_service.infrastructure.adapters;

import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.domain.ports.out.UserRepositoryPort;
import com.coopcredit.creddit_application_service.infrastructure.entities.UserEntity;
import com.coopcredit.creddit_application_service.infrastructure.mappers.auth.UserMapper;
import com.coopcredit.creddit_application_service.infrastructure.repositories.JpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
