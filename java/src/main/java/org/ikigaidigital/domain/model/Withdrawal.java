package org.ikigaidigital.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Withdrawal {
    private Integer id;
    private Integer timeDepositId;
    private BigDecimal amount;
    private LocalDate date;
}
