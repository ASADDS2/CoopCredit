package com.coopcredit.creddit_application_service.domain.ports.in.affiliates;

import com.coopcredit.creddit_application_service.domain.model.Affiliate;

public interface RegisterAffiliateUseCase {

    Affiliate execute(Affiliate affiliate);
}
