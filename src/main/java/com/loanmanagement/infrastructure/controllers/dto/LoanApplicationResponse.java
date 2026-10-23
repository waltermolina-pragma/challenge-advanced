package com.loanmanagement.infrastructure.controllers.dto;

import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.model.Customer;
import com.loanmanagement.domain.model.LoanApplication;
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
    String loanTerm,
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
    public LoanApplicationResponse(
            UUID id,
            UUID customerId,
            BigDecimal requestedAmount,
            Integer loanTerm,
            String purpose,
            String status,
            LocalDate applicationDate,
            LocalDateTime approvalDate) {
        this(id, customerId, null, null, requestedAmount, 
             loanTerm != null ? loanTerm.toString() : null, purpose, status, 
             applicationDate, approvalDate, null, null, null, null, null, null, null, null);
    }

    public static LoanApplicationResponse fromDomain(
            com.loanmanagement.domain.model.LoanApplication loanApplication,
            com.loanmanagement.domain.model.Customer customer,
            com.loanmanagement.domain.model.RiskEvaluation riskEvaluation) {

        String customerName = customer != null ? customer.getName() : null;
        String customerIdentification = customer != null ? customer.getIdentificationNumber() : null;

        String riskEvalStatus = riskEvaluation != null ? riskEvaluation.getEvaluationStatus() : null;
        String riskLevel = riskEvaluation != null ? riskEvaluation.getRiskLevel() : null;
        String evaluator = riskEvaluation != null ? riskEvaluation.getEvaluator() : null;
        LocalDateTime evalDate = riskEvaluation != null ? riskEvaluation.getEvaluationDate() : null;
        String evalComments = riskEvaluation != null ? riskEvaluation.getComments() : null;

        LocalDate appDate = loanApplication.getApplicationDate() != null ? loanApplication.getApplicationDate().toLocalDate() : null;

        return new LoanApplicationResponse(
            loanApplication.getId(),
            loanApplication.getCustomerId(),
            customerName,
            customerIdentification,
            loanApplication.getRequestedAmount(),
            loanApplication.getLoanTerm(),
            loanApplication.getPurpose(),
            loanApplication.getStatus(),
            appDate,
            loanApplication.getApprovalDate(),
            loanApplication.getCollateralType(),
            loanApplication.getCollateralValue(),
            loanApplication.getAdditionalNotes(),
            riskEvalStatus,
            riskLevel,
            evaluator,
            evalDate,
            evalComments
        );
    }
}