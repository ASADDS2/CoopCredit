package com.coopcredit.creddit_application_service.domain.ports.in.auth;

import com.coopcredit.creddit_application_service.domain.model.User;

public interface RegisterUserUseCase {

    User execute(User user);
}
