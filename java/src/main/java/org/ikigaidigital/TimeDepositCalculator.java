package org.ikigaidigital;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.strategy.InterestStrategy;
import org.ikigaidigital.domain.service.strategy.InterestStrategyFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class TimeDepositCalculator {

    private final InterestStrategyFactory strategyFactory;

    public TimeDepositCalculator(InterestStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit x : xs) {
            InterestStrategy strategy = strategyFactory.getStrategy(x.getPlanType());
            BigDecimal interest = strategy.calculateInterest(x);

            BigDecimal a2d = x.getBalance()
                    .add(interest.setScale(2, RoundingMode.HALF_UP));
            x.setBalance(a2d);
        }
    }
}
