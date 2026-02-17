package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

    @Query("SELECT DISTINCT td FROM TimeDepositEntity td LEFT JOIN FETCH td.withdrawals")
    List<TimeDepositEntity> findAllWithWithdrawals();
}
