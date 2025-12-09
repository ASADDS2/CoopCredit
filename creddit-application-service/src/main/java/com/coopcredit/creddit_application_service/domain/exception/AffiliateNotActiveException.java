package com.coopcredit.creddit_application_service.domain.exception;

public class AffiliateNotActiveException extends BusinessRuleException {

    public AffiliateNotActiveException(String document) {
        super(String.format("Affiliate with document %s is not active and cannot apply for credit", document));
    }

    public AffiliateNotActiveException(Long affiliateId) {
        super(String.format("Affiliate with ID %d is not active and cannot apply for credit", affiliateId));
    }
}
