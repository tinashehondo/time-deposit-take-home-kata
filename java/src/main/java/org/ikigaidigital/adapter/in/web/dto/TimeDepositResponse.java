package org.ikigaidigital.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Time deposit information")
public class TimeDepositResponse {

    @Schema(description = "Unique identifier of the time deposit", example = "1")
    private Integer id;

    @Schema(description = "Type of plan (basic, student, premium)", example = "premium")
    private String planType;

    @Schema(description = "Current balance", example = "10000.50")
    private BigDecimal balance;

    @Schema(description = "Number of days the deposit has been active", example = "120")
    private Integer days;

    @Schema(description = "List of withdrawals made from this deposit")
    private List<WithdrawalResponse> withdrawals;
}
