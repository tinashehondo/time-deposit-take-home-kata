package org.ikigaidigital.domain.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterestStrategyFactoryTest {

    private InterestStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new InterestStrategyFactory(List.of(
                new BasicInterestStrategy(),
                new StudentInterestStrategy(),
                new PremiumInterestStrategy()
        ));
    }

    @Test
    void shouldReturnBasicStrategy() {
        InterestStrategy strategy = factory.getStrategy("basic");
        assertInstanceOf(BasicInterestStrategy.class, strategy);
    }

    @Test
    void shouldReturnStudentStrategy() {
        InterestStrategy strategy = factory.getStrategy("student");
        assertInstanceOf(StudentInterestStrategy.class, strategy);
    }

    @Test
    void shouldReturnPremiumStrategy() {
        InterestStrategy strategy = factory.getStrategy("premium");
        assertInstanceOf(PremiumInterestStrategy.class, strategy);
    }

    @Test
    void shouldThrowForUnknownPlanType() {
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy("unknown"));
    }
}
