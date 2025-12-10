package com.coopcredit.creddit_application_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.coopcredit.creddit_application_service.infrastructure.repositories")
@EntityScan(basePackages = "com.coopcredit.creddit_application_service.infrastructure.entities")
public class CredditApplicationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CredditApplicationServiceApplication.class, args);
	}

}
