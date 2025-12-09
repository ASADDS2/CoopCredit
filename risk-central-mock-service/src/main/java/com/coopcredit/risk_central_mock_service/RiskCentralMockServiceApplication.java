package com.coopcredit.risk_central_mock_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.coopcredit.risk_central_mock_service")
@Slf4j
public class RiskCentralMockServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiskCentralMockServiceApplication.class, args);
		log.info("=================================================");
		log.info("  Risk Central Mock Service Started Successfully");
		log.info("  Port: 8081");
		log.info("  Swagger UI: http://localhost:8081/swagger-ui.html");
		log.info("  API Docs: http://localhost:8081/api-docs");
		log.info("  Actuator: http://localhost:8081/actuator");
		log.info("=================================================");
	}

}
