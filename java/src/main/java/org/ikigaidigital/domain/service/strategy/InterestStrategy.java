package org.ikigaidigital.domain.service.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;

import java.math.BigDecimal;

public interface InterestStrategy {

    BigDecimal calculateInterest(TimeDeposit deposit);

    String getPlanType();
}
