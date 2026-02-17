package org.ikigaidigital.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Withdrawal information")
public class WithdrawalResponse {

    @Schema(description = "Unique identifier of the withdrawal", example = "1")
    private Integer id;

    @Schema(description = "ID of the parent time deposit", example = "1")
    private Integer timeDepositId;

    @Schema(description = "Withdrawal amount", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "Date of the withdrawal", example = "2024-01-15")
    private LocalDate date;
}
