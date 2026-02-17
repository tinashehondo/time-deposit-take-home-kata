package org.ikigaidigital.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ikigaidigital.adapter.in.web.dto.TimeDepositResponse;
import org.ikigaidigital.adapter.in.web.dto.WithdrawalResponse;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-deposits")
@Tag(name = "Time Deposits", description = "Time deposit management endpoints")
public class TimeDepositController {

    private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
    private final UpdateBalancesUseCase updateBalancesUseCase;

    public TimeDepositController(
            GetAllTimeDepositsUseCase getAllTimeDepositsUseCase,
            UpdateBalancesUseCase updateBalancesUseCase) {
        this.getAllTimeDepositsUseCase = getAllTimeDepositsUseCase;
        this.updateBalancesUseCase = updateBalancesUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Get all time deposits",
            description = "Retrieves a list of all time deposits with their current balances and withdrawal history"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all time deposits")
    public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
        List<TimeDeposit> timeDeposits = getAllTimeDepositsUseCase.getAllTimeDeposits();

        List<TimeDepositResponse> responses = timeDeposits.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/update-balances")
    @Operation(
            summary = "Update all time deposit balances",
            description = "Calculates and applies monthly interest to all time deposits based on their plan type and duration"
    )
    @ApiResponse(responseCode = "200", description = "Successfully updated all balances")
    public ResponseEntity<Void> updateBalances() {
        updateBalancesUseCase.updateBalances();
        return ResponseEntity.ok().build();
    }

    private TimeDepositResponse toResponse(TimeDeposit domain) {
        List<WithdrawalResponse> withdrawalResponses = domain.getWithdrawals().stream()
                .map(w -> new WithdrawalResponse(w.getId(), w.getTimeDepositId(), w.getAmount(), w.getDate()))
                .collect(Collectors.toList());

        return new TimeDepositResponse(
                domain.getId(),
                domain.getPlanType(),
                domain.getBalance(),
                domain.getDays(),
                withdrawalResponses
        );
    }
}
