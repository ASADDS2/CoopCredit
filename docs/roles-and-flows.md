# Roles and Business Flows

## Roles

- ROLE_AFILIADO: Can submit and view their own credit applications.
- ROLE_ANALISTA: Can view pending applications and evaluate them.
- ROLE_ADMIN: Full access to affiliates and applications.

## Authentication

- Register: `POST /api/auth/register`
- Login: `POST /api/auth/login` → returns JWT.
- Include `Authorization: Bearer <token>` in all protected requests.

## Flows

- Register Affiliate (Admin)
  1. Admin logs in.
  2. `POST /api/affiliates` to create an affiliate.

- Submit Credit Application (Afiliado)
  1. Afiliado logs in.
  2. `POST /api/credit-applications` with amount and term.
  3. The application is created in PENDING state.

- Evaluate Credit Application (Analista/Admin)
  1. Analyst/Admin logs in.
  2. `POST /api/credit-applications/{id}/evaluate` triggers Risk Central evaluation via adapter.
  3. Application is updated with RiskEvaluation and decision.

## Authorization Rules

- Afiliado can only access their own applications.
- Analyst can access only pending applications.
- Admin has full access.
