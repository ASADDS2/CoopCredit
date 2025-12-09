-- V1__create_base_schema.sql
-- =========================================
-- CoopCredit Database Schema Creation
-- =========================================
-- Create affiliates table
CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    salary DECIMAL(15, 2) NOT NULL CHECK (salary > 0),
    affiliation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Create index on document for faster lookups
CREATE INDEX idx_affiliates_document ON affiliates(document);
CREATE INDEX idx_affiliates_status ON affiliates(status);
-- Create credit_applications table
CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL CHECK (requested_amount > 0),
    term_months INTEGER NOT NULL CHECK (term_months > 0),
    proposed_rate DECIMAL(5, 2) NOT NULL CHECK (proposed_rate >= 0),
    application_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_credit_application_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id) ON DELETE CASCADE
);
-- Create indexes for credit_applications
CREATE INDEX idx_credit_applications_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_credit_applications_status ON credit_applications(status);
CREATE INDEX idx_credit_applications_date ON credit_applications(application_date);
-- Create risk_evaluations table
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_application_id BIGINT NOT NULL UNIQUE,
    document VARCHAR(50) NOT NULL,
    score INTEGER NOT NULL CHECK (
        score >= 0
        AND score <= 1000
    ),
    risk_level VARCHAR(20) NOT NULL CHECK (risk_level IN ('BAJO', 'MEDIO', 'ALTO')),
    detail VARCHAR(500),
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_evaluation_application FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id) ON DELETE CASCADE
);
-- Create index on credit_application_id
CREATE INDEX idx_risk_evaluations_application ON risk_evaluations(credit_application_id);
-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (
        role IN ('ROLE_AFILIADO', 'ROLE_ANALISTA', 'ROLE_ADMIN')
    ),
    affiliate_id BIGINT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id) ON DELETE
    SET NULL
);
-- Create index on username
CREATE INDEX idx_users_username ON users(username);
-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- Create triggers for updated_at
CREATE TRIGGER update_affiliates_updated_at BEFORE
UPDATE ON affiliates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_credit_applications_updated_at BEFORE
UPDATE ON credit_applications FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE
UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();