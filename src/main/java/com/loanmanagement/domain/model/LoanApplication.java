package com.loanmanagement.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
public class LoanApplication {
    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private String purpose;
    private String status;
    private LocalDateTime applicationDate;
    private LocalDateTime lastUpdateDate;
    private String evaluatedBy;
    private String rejectionReason;
    private BigDecimal approvedAmount;
    private Integer approvedTerm;
    private BigDecimal interestRate;

    @Transient
    private RiskEvaluation riskEvaluation;

    @Transient
    private Customer customer;

    private String loanTerm;

    private LocalDateTime approvalDate;

    private String collateralType;

    private BigDecimal collateralValue;

    private String additionalNotes;

    private String approvalConditions;

    public LoanApplication() {
    }

    public LoanApplication(UUID id, UUID customerId, BigDecimal requestedAmount,
                           Integer termMonths, String purpose) {
        this.id = id;
        this.customerId = customerId;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = LoanStatus.PENDING_EVALUATION.name();
        this.applicationDate = LocalDateTime.now();
        this.lastUpdateDate = LocalDateTime.now();
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdateDate = LocalDateTime.now();
    }

    public void validateApplication() {
        if (this.getCustomerId() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (this.getRequestedAmount() == null || this.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor a cero");
        }
        if (this.getRequestedAmount().compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("El monto solicitado excede el límite máximo de $1,000,000");
        }
        if (this.getTermMonths() == null || this.getTermMonths() <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero meses");
        }
        if (this.getTermMonths() > 360) {
            throw new IllegalArgumentException("El plazo no puede exceder 360 meses (30 años)");
        }
    }
}