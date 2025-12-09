package com.coopcredit.creddit_application_service.infrastructure.mappers.auth;

import com.coopcredit.creddit_application_service.domain.model.User;
import com.coopcredit.creddit_application_service.infrastructure.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole(),
                entity.getAffiliateId(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setAffiliateId(domain.getAffiliateId());
        entity.setEnabled(domain.isEnabled());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
