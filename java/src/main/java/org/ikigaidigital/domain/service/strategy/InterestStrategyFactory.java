package org.ikigaidigital.domain.service.strategy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InterestStrategyFactory {

    private final Map<String, InterestStrategy> strategyMap;

    public InterestStrategyFactory(List<InterestStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(InterestStrategy::getPlanType, Function.identity()));
    }

    public InterestStrategy getStrategy(String planType) {
        InterestStrategy strategy = strategyMap.get(planType);
        if (strategy == null) {
            throw new IllegalArgumentException("No interest strategy found for plan type: " + planType);
        }
        return strategy;
    }
}
