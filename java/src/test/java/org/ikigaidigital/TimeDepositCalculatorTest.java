package org.ikigaidigital;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.service.TimeDepositCalculator;
import org.ikigaidigital.domain.service.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.service.strategy.InterestStrategyFactory;
import org.ikigaidigital.domain.service.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.service.strategy.StudentInterestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeDepositCalculatorTest {

    private TimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        InterestStrategyFactory factory = new InterestStrategyFactory(List.of(
                new BasicInterestStrategy(),
                new StudentInterestStrategy(),
                new PremiumInterestStrategy()
        ));
        calculator = new TimeDepositCalculator(factory);
    }

    /*@Test
    public void updateBalance_Test() {
        List<TimeDeposit> plans = Arrays.asList(
                new TimeDeposit(1,"basic", 1234567.00, 45)
        );
        calculator.updateBalance(plans);

        assertThat(1).isEqualTo(1);
    }*/
    @Test
    void shouldNotApplyInterestForFirst30Days() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "basic", 25, BigDecimal.valueOf(10000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(10000.0).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }
    @Test
    void shouldApplyBasicPlanInterest() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "basic", 45, BigDecimal.valueOf(10000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(10008.33).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldApplyStudentPlanInterestUnder366Days() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "student", 90, BigDecimal.valueOf(5000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(5012.5).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldNotApplyStudentPlanInterestAfter365Days() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "student", 400, BigDecimal.valueOf(5000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(5000.0).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldApplyPremiumPlanInterestAfter45Days() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "premium", 60, BigDecimal.valueOf(20000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(20083.33).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldNotApplyPremiumPlanInterestBefore45Days() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "premium", 40, BigDecimal.valueOf(20000.0)));

        calculator.updateBalance(deposits);

        assertEquals(BigDecimal.valueOf(20000.0).setScale(2, RoundingMode.HALF_EVEN), deposits.get(0).getBalance().setScale(2, RoundingMode.HALF_EVEN));
    }
}
