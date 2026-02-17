package org.ikigaidigital.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDeposit {

    private Integer id;
    private String planType;
    private Integer days;
    private BigDecimal balance;
    private List<Withdrawal> withdrawals = new ArrayList<>();

    public TimeDeposit(Integer id, String planType, Integer days, BigDecimal balance) {
        this.id = id;
        this.planType = planType;
        this.days = days;
        this.balance = balance;
        this.withdrawals = new ArrayList<>();
    }
}
