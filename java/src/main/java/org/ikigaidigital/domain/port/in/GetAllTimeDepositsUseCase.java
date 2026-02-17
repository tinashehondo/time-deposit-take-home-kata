package org.ikigaidigital.domain.port.in;

import org.ikigaidigital.domain.model.TimeDeposit;

import java.util.List;

public interface GetAllTimeDepositsUseCase {
    List<TimeDeposit> getAllTimeDeposits();
}
