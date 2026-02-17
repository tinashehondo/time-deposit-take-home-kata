package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository jpaRepository;

    public TimeDepositPersistenceAdapter(TimeDepositJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TimeDeposit> findAll() {
        return jpaRepository.findAllWithWithdrawals().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<TimeDeposit> timeDeposits) {
        List<TimeDepositEntity> entities = timeDeposits.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    private TimeDeposit toDomain(TimeDepositEntity entity) {
        List<Withdrawal> withdrawals = entity.getWithdrawals().stream()
                .map(w -> new Withdrawal(w.getId(), entity.getId(), w.getAmount(), w.getDate()))
                .collect(Collectors.toList());

        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getDays(),
                entity.getBalance(),
                withdrawals
        );
    }

    private TimeDepositEntity toEntity(TimeDeposit domain) {
        TimeDepositEntity entity = new TimeDepositEntity();
        entity.setId(domain.getId());
        entity.setPlanType(domain.getPlanType());
        entity.setDays(domain.getDays());
        entity.setBalance(domain.getBalance());

        if (domain.getWithdrawals() != null) {
            List<WithdrawalEntity> withdrawalEntities = domain.getWithdrawals().stream()
                    .map(w -> {
                        WithdrawalEntity we = new WithdrawalEntity();
                        we.setId(w.getId());
                        we.setTimeDeposit(entity);
                        we.setAmount(w.getAmount());
                        we.setDate(w.getDate());
                        return we;
                    })
                    .collect(Collectors.toList());
            entity.setWithdrawals(withdrawalEntities);
        }

        return entity;
    }
}
