package org.ikigaidigital.domain.port.out;

import org.ikigaidigital.domain.model.TimeDeposit;

import java.util.List;

public interface TimeDepositRepository {
    List<TimeDeposit> findAll();
    void saveAll(List<TimeDeposit> timeDeposits);
}
