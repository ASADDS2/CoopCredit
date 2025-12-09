package com.coopcredit.creddit_application_service.domain.ports.in.auth;

public interface AuthenticateUserUseCase {

    String execute(String username, String password);
}
