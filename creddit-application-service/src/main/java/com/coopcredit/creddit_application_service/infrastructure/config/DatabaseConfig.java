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

        // Try to build database URL from individual environment variables (Render
        // style)
        String dbHost = getEnvVar("DB_HOST");
        String dbPort = getEnvVar("DB_PORT", "5432");
        String dbName = getEnvVar("DB_NAME");
        String dbUsername = getEnvVar("DB_USERNAME");
        String dbPassword = getEnvVar("DB_PASSWORD");

        logger.info("DB_HOST: {}", dbHost != null ? "found" : "null");
        logger.info("DB_NAME: {}", dbName != null ? "found" : "null");
        logger.info("DB_USERNAME: {}", dbUsername != null ? "found" : "null");
        logger.info("DB_PASSWORD: {}", dbPassword != null ? "****" : "null");

        HikariConfig config = new HikariConfig();

        // If individual variables are set, build the URL from them
        if (dbHost != null && dbName != null && dbUsername != null && dbPassword != null) {
            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", dbHost, dbPort, dbName);
            logger.info("Built JDBC URL from individual env vars for host: {}", dbHost);

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
        } else {
            // Fallback: Try DATABASE_URL or SPRING_DATASOURCE_URL
            String databaseUrl = getEnvVar("DATABASE_URL");
            if (databaseUrl == null) {
                databaseUrl = getEnvVar("SPRING_DATASOURCE_URL");
            }
            if (databaseUrl == null) {
                databaseUrl = environment.getProperty("spring.datasource.url");
            }

            if (databaseUrl == null || databaseUrl.isEmpty()) {
                logger.error("No database configuration found!");
                logger.error("Expected: DB_HOST, DB_NAME, DB_USERNAME, DB_PASSWORD or DATABASE_URL");
                throw new IllegalStateException(
                        "Database configuration not found. " +
                                "Please set DB_HOST, DB_NAME, DB_USERNAME, DB_PASSWORD environment variables.");
            }

            logger.info("Using DATABASE_URL/SPRING_DATASOURCE_URL");

            try {
                // Handle both formats: postgresql:// and jdbc:postgresql://
                if (databaseUrl.startsWith("jdbc:")) {
                    config.setJdbcUrl(databaseUrl);
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
                if (port == -1)
                    port = 5432;

                String database = dbUri.getPath();
                if (database != null && database.startsWith("/")) {
                    database = database.substring(1);
                }

                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, database);
                logger.info("JDBC URL configured for host: {}", host);

                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
            } catch (URISyntaxException e) {
                logger.error("Failed to parse DATABASE_URL");
                throw e;
            }
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

    private String getEnvVar(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            value = environment.getProperty(name);
        }
        return (value != null && !value.isEmpty()) ? value : null;
    }

    private String getEnvVar(String name, String defaultValue) {
        String value = getEnvVar(name);
        return value != null ? value : defaultValue;
    }
}
