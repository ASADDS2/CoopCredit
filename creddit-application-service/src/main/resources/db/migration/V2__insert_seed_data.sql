-- V2__insert_seed_data.sql
-- =========================================
-- CoopCredit Initial Seed Data
-- =========================================
-- Insert sample affiliates
INSERT INTO affiliates (document, name, salary, affiliation_date, status)
VALUES (
        '12345678',
        'Juan Pérez González',
        3500000.00,
        '2023-01-15',
        'ACTIVE'
    ),
    (
        '87654321',
        'María López Ramírez',
        4200000.00,
        '2023-03-20',
        'ACTIVE'
    ),
    (
        '11223344',
        'Carlos Martínez Silva',
        2800000.00,
        '2023-06-10',
        'ACTIVE'
    ),
    (
        '44332211',
        'Ana García Torres',
        5100000.00,
        '2022-12-05',
        'ACTIVE'
    ),
    (
        '55667788',
        'Pedro Rodríguez Cruz',
        3900000.00,
        '2024-11-01',
        'ACTIVE'
    ),
    (
        '99887766',
        'Laura Fernández Díaz',
        2500000.00,
        '2024-10-20',
        'INACTIVE'
    );
-- Insert users (password: "password123" encrypted with BCrypt round 12)
-- BCrypt hash for "password123" -> $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TupxU7xvGP8K8VJqN0NK5Xb3tX4i
INSERT INTO users (username, password, role, affiliate_id, enabled)
VALUES (
        'admin',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TupxU7xvGP8K8VJqN0NK5Xb3tX4i',
        'ROLE_ADMIN',
        NULL,
        TRUE
    ),
    (
        'analista1',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TupxU7xvGP8K8VJqN0NK5Xb3tX4i',
        'ROLE_ANALISTA',
        NULL,
        TRUE
    ),
    (
        'afiliado1',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TupxU7xvGP8K8VJqN0NK5Xb3tX4i',
        'ROLE_AFILIADO',
        1,
        TRUE
    ),
    (
        'afiliado2',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TupxU7xvGP8K8VJqN0NK5Xb3tX4i',
        'ROLE_AFILIADO',
        2,
        TRUE
    );
-- Insert sample credit applications
INSERT INTO credit_applications (
        affiliate_id,
        requested_amount,
        term_months,
        proposed_rate,
        application_date,
        status,
        rejection_reason
    )
VALUES (
        1,
        10000000.00,
        36,
        12.50,
        '2024-12-01',
        'PENDING',
        NULL
    ),
    (
        2,
        15000000.00,
        48,
        11.75,
        '2024-12-02',
        'PENDING',
        NULL
    ),
    (
        4,
        20000000.00,
        60,
        13.00,
        '2024-11-28',
        'APPROVED',
        NULL
    );
-- Insert sample risk evaluation (for the approved application)
INSERT INTO risk_evaluations (
        credit_application_id,
        document,
        score,
        risk_level,
        detail
    )
VALUES (
        3,
        '44332211',
        750,
        'BAJO',
        'Evaluación positiva basada en historial crediticio y capacidad de pago'
    );