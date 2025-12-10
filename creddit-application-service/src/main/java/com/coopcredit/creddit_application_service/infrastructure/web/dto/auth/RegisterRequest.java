package com.coopcredit.creddit_application_service.infrastructure.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(ROLE_AFILIADO|ROLE_ANALISTA|ROLE_ADMIN)$", message = "Role must be one of: ROLE_AFILIADO, ROLE_ANALISTA, ROLE_ADMIN")
    private String role;

    private Long affiliateId; // Optional, only for ROLE_AFILIADO
}
