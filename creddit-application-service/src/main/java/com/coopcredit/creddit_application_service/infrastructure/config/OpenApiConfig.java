package com.coopcredit.creddit_application_service.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "CoopCredit Application Service API", version = "1.0.0", description = "Credit application management system for CoopCredit cooperative", contact = @Contact(name = "CoopCredit IT Team", email = "it@coopcredit.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")), servers = {
                @Server(description = "Current Server", url = "/"),
                @Server(description = "Local Development", url = "http://localhost:8080"),
                @Server(description = "Render Production", url = "https://creddit-application-service.onrender.com")
})
@SecurityScheme(name = "bearer-jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
