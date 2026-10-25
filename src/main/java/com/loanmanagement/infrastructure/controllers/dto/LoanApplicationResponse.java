package com.loanmanagement.infrastructure.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanApplicationResponse(
        UUID id,
        UUID customerId,
        String customerName,
        String customerIdentification,
        BigDecimal requestedAmount,
        String termMonths,
        String purpose,
        String status,
        LocalDate applicationDate,
        LocalDateTime approvalDate,
        String collateralType,
        BigDecimal collateralValue,
        String additionalNotes,
        String riskEvaluationStatus,
        String riskLevel,
        String evaluator,
        LocalDateTime evaluationDate,
        String evaluationComments
) {
}