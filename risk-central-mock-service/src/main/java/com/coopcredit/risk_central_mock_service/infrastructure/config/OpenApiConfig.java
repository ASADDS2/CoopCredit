package com.coopcredit.risk_central_mock_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Risk Central Mock Service API")
                        .version("1.0.0")
                        .description("Lightweight risk evaluation service for CoopCredit. " +
                                "Provides consistent risk scoring based on document identification.")
                        .contact(new Contact()
                                .name("CoopCredit Dev Team")
                                .email("dev@coopcredit.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
