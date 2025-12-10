package com.coopcredit.creddit_application_service.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

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

    private final Environment environment;

    public DatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource() throws URISyntaxException {
        logger.info("=== DatabaseConfig: Starting DataSource configuration ===");

        // Log all environment variables containing DATABASE or DB for debugging
        logger.info("Searching for database-related environment variables...");
        System.getenv().forEach((key, value) -> {
            if (key.toUpperCase().contains("DATABASE") || key.toUpperCase().contains("DB") ||
                    key.toUpperCase().contains("POSTGRES") || key.toUpperCase().contains("PG")) {
                logger.info("Found env var: {}={}", key, key.contains("PASSWORD") ? "****"
                        : (value != null && value.length() > 20 ? value.substring(0, 20) + "..." : value));
            }
        });

        // Try multiple sources for database URL
        String databaseUrl = System.getenv("DATABASE_URL");
        logger.info("System.getenv('DATABASE_URL'): {}", databaseUrl != null ? "found" : "null");

        // Try SPRING_DATASOURCE_URL (used in Render)
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = System.getenv("SPRING_DATASOURCE_URL");
            logger.info("System.getenv('SPRING_DATASOURCE_URL'): {}", databaseUrl != null ? "found" : "null");
        }

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = environment.getProperty("spring.datasource.url");
            logger.info("environment.getProperty('spring.datasource.url'): {}", databaseUrl != null ? "found" : "null");
        }

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = environment.getProperty("DATABASE_URL");
            logger.info("environment.getProperty('DATABASE_URL'): {}", databaseUrl != null ? "found" : "null");
        }

        logger.info("DATABASE_URL from env: {}", databaseUrl != null ? "found" : "not found");

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            logger.error("DATABASE_URL not found anywhere!");
            logger.error("Active profiles: {}", String.join(", ", environment.getActiveProfiles()));
            throw new IllegalStateException(
                    "DATABASE_URL environment variable is not set. " +
                            "Please configure DATABASE_URL in Render environment variables. " +
                            "Check the logs above for available database-related variables.");
        }

        logger.info("DATABASE_URL found, proceeding with configuration...");

        logger.info("Configuring database from DATABASE_URL");

        HikariConfig config = new HikariConfig();

        try {
            // Handle both formats: postgresql:// and jdbc:postgresql://
            if (databaseUrl.startsWith("jdbc:")) {
                // Already in JDBC format
                logger.info("DATABASE_URL already in JDBC format");
                config.setJdbcUrl(databaseUrl);
                // Extract credentials from URL if present
                return createDataSourceFromJdbcUrl(config, databaseUrl);
            }

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

            // Convert to JDBC format with SSL (required for cloud databases like Render)
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, database);

            logger.info("JDBC URL configured for host: {}", host);

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

        } catch (URISyntaxException e) {
            logger.error("Failed to parse DATABASE_URL: {}", databaseUrl);
            throw e;
        }

        // HikariCP settings
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    private DataSource createDataSourceFromJdbcUrl(HikariConfig config, String jdbcUrl) {
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }
}
