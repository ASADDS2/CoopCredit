package com.coopcredit.creddit_application_service.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Database configuration for production environment.
 * Handles conversion of DATABASE_URL format to JDBC format.
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set");
        }

        logger.info("Configuring database from DATABASE_URL");

        HikariConfig config = new HikariConfig();

        try {
            // Parse DATABASE_URL format: postgresql://user:pass@host:port/database
            URI dbUri = new URI(databaseUrl);

            String userInfo = dbUri.getUserInfo();
            if (userInfo == null) {
                throw new IllegalStateException("DATABASE_URL does not contain user credentials");
            }

            String[] credentials = userInfo.split(":");
            String username = credentials[0];
            String password = credentials.length > 1 ? credentials[1] : "";

            String host = dbUri.getHost();
            int port = dbUri.getPort();
            if (port == -1) {
                port = 5432; // Default PostgreSQL port
            }

            String database = dbUri.getPath();
            if (database != null && database.startsWith("/")) {
                database = database.substring(1);
            }

            // Convert to JDBC format
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

            // Add SSL if needed (Render requires SSL)
            if (host != null && host.contains("render.com")) {
                jdbcUrl += "?sslmode=require";
            }

            logger.info("JDBC URL configured: {}", jdbcUrl.replaceAll(":[^:@]+@", ":****@"));

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

        } catch (URISyntaxException e) {
            logger.error("Failed to parse DATABASE_URL", e);
            throw e;
        }

        // HikariCP settings
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }
}
