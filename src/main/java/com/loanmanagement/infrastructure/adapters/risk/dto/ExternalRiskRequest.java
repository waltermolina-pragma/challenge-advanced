package com.loanmanagement.infrastructure.adapters.risk.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExternalRiskRequest(
        String customerId,
        String identificationNumber,
        BigDecimal monthlyIncome,
        BigDecimal requestedAmount,
        Integer termMonths,
        String employmentStatus,
        String creditScore
) {
}
