package org.ikigaidigital.domain.service;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TimeDepositService implements GetAllTimeDepositsUseCase, UpdateBalancesUseCase {

    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public TimeDepositService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeDeposit> getAllTimeDeposits() {
        return repository.findAll();
    }

    @Override
    public void updateBalances() {
        List<TimeDeposit> timeDeposits = repository.findAll();
        calculator.updateBalance(timeDeposits);
        repository.saveAll(timeDeposits);
    }
}
