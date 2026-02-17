package org.ikigaidigital.domain.service.strategy;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestStrategyTest {
    

    @Test
    void basicShouldReturnCorrectPlanType() {
        assertEquals("basic", new BasicInterestStrategy().getPlanType());
    }

    @Test
    void basicShouldReturnZeroForFirst30Days() {
        BasicInterestStrategy strategy = new BasicInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "basic", 30, BigDecimal.valueOf(10000.0));
        assertEquals(BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void basicShouldCalculateInterestAfter30Days() {
        BasicInterestStrategy strategy = new BasicInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "basic", 31, BigDecimal.valueOf(10000.0));
        BigDecimal expected = BigDecimal.valueOf(10000.0)
                .multiply(new BigDecimal("0.01"))
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        assertEquals(expected.setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

   

    @Test
    void studentShouldReturnCorrectPlanType() {
        assertEquals("student", new StudentInterestStrategy().getPlanType());
    }

    @Test
    void studentShouldReturnZeroForFirst30Days() {
        StudentInterestStrategy strategy = new StudentInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "student", 25, BigDecimal.valueOf(5000.0));
        assertEquals(BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void studentShouldCalculateInterestBetween31And365Days() {
        StudentInterestStrategy strategy = new StudentInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "student", 90, BigDecimal.valueOf(5000.0));
        BigDecimal expected = BigDecimal.valueOf(5000.0)
                .multiply(new BigDecimal("0.03"))
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        assertEquals(expected.setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void studentShouldReturnZeroAt366DaysOrMore() {
        StudentInterestStrategy strategy = new StudentInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "student", 366, BigDecimal.valueOf(5000.0));
        assertEquals(BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void studentShouldReturnInterestAt365Days() {
        StudentInterestStrategy strategy = new StudentInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "student", 365, BigDecimal.valueOf(5000.0));
        BigDecimal expected =  BigDecimal.valueOf(5000.0)
                .multiply(new BigDecimal("0.03"))
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        assertEquals(expected.setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }
    

    @Test
    void premiumShouldReturnCorrectPlanType() {
        assertEquals("premium", new PremiumInterestStrategy().getPlanType());
    }

    @Test
    void premiumShouldReturnZeroFor45DaysOrLess() {
        PremiumInterestStrategy strategy = new PremiumInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "premium", 45, BigDecimal.valueOf(20000.0));
        assertEquals(BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void premiumShouldCalculateInterestAfter45Days() {
        PremiumInterestStrategy strategy = new PremiumInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "premium", 46, BigDecimal.valueOf(20000.0));
        BigDecimal expected = BigDecimal.valueOf(20000.0)
                .multiply(new BigDecimal("0.05"))
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        assertEquals(expected.setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void premiumShouldReturnZeroForFirst30Days() {
        PremiumInterestStrategy strategy = new PremiumInterestStrategy();
        TimeDeposit deposit = new TimeDeposit(1, "premium", 25, BigDecimal.valueOf(20000.0));
        assertEquals(BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_EVEN), strategy.calculateInterest(deposit).setScale(2, RoundingMode.HALF_EVEN));
    }
}
