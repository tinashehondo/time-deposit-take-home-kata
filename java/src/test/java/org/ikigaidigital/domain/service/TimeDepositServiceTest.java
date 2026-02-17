package org.ikigaidigital.domain.service;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeDepositServiceTest {

    @Mock
    private TimeDepositRepository repository;

    @Mock
    private TimeDepositCalculator calculator;

    private TimeDepositService service;

    @BeforeEach
    void setUp() {
        service = new TimeDepositService(repository, calculator);
    }

    @Test
    void getAllTimeDeposits_shouldReturnAllDeposits() {
        List<TimeDeposit> expected = List.of(
                new TimeDeposit(1, "basic", 45, BigDecimal.valueOf(10000.0)),
                new TimeDeposit(2, "student", 90, BigDecimal.valueOf(5000.0))
        );
        when(repository.findAll()).thenReturn(expected);

        List<TimeDeposit> result = service.getAllTimeDeposits();

        assertEquals(2, result.size());
        assertEquals("basic", result.get(0).getPlanType());
        assertEquals("student", result.get(1).getPlanType());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllTimeDeposits_shouldReturnEmptyListWhenNoDeposits() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<TimeDeposit> result = service.getAllTimeDeposits();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllTimeDeposits_shouldIncludeWithdrawals() {
        List<Withdrawal> withdrawals = List.of(
                new Withdrawal(1, 1, new BigDecimal("500.00"), LocalDate.of(2024, 1, 15))
        );
        TimeDeposit deposit = new TimeDeposit(1, "basic", 45, BigDecimal.valueOf(10000.0), new ArrayList<>(withdrawals));
        when(repository.findAll()).thenReturn(List.of(deposit));

        List<TimeDeposit> result = service.getAllTimeDeposits();

        assertEquals(1, result.get(0).getWithdrawals().size());
        assertEquals(new BigDecimal("500.00"), result.get(0).getWithdrawals().get(0).getAmount());
    }

    @Test
    void updateBalances_shouldFetchCalculateAndSave() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "basic", 45, BigDecimal.valueOf(10000.0)));
        when(repository.findAll()).thenReturn(deposits);

        service.updateBalances();

        verify(repository, times(1)).findAll();
        verify(calculator, times(1)).updateBalance(deposits);
        verify(repository, times(1)).saveAll(deposits);
    }

    @Test
    void updateBalances_shouldCallInCorrectOrder() {
        List<TimeDeposit> deposits = new ArrayList<>();
        deposits.add(new TimeDeposit(1, "basic", 45, BigDecimal.valueOf(10000.0)));
        when(repository.findAll()).thenReturn(deposits);

        service.updateBalances();

        InOrder inOrder = inOrder(repository, calculator);
        inOrder.verify(repository).findAll();
        inOrder.verify(calculator).updateBalance(deposits);
        inOrder.verify(repository).saveAll(deposits);
    }

    @Test
    void updateBalances_withEmptyList_shouldStillCallCalculatorAndSave() {
        List<TimeDeposit> empty = new ArrayList<>();
        when(repository.findAll()).thenReturn(empty);

        service.updateBalances();

        verify(calculator, times(1)).updateBalance(empty);
        verify(repository, times(1)).saveAll(empty);
    }
}
