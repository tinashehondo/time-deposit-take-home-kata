package org.ikigaidigital.domain.service.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BasicInterestStrategy implements InterestStrategy {

    @Override
    public BigDecimal calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() > 30) {
            return deposit.getBalance()
                    .multiply(new BigDecimal("0.01"))
                    .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(0);
    }

    @Override
    public String getPlanType() {
        return "basic";
    }
}
