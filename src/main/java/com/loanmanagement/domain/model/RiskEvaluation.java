package com.loanmanagement.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "risk_evaluations")
@Getter
@Setter
public class RiskEvaluation {
    @Id
    private UUID id;
    private UUID loanApplicationId;
    private String evaluationStatus;
    private String riskLevel;
    private String evaluator;
    private LocalDateTime evaluationDate;
    private String comments;

    public void validate() {
        Objects.requireNonNull(id, "Risk evaluation ID cannot be null");

        Objects.requireNonNull(loanApplicationId, "Loan application ID cannot be null");

        Objects.requireNonNull(evaluationDate, "Evaluation date cannot be null");

        if (evaluationStatus == null || evaluationStatus.isBlank()) {
            throw new IllegalArgumentException("Evaluation status cannot be blank");
        }

        if (riskLevel == null || riskLevel.isBlank()) {
            throw new IllegalArgumentException("Risk level cannot be blank");
        }

        if (evaluator == null || evaluator.isBlank()) {
            throw new IllegalArgumentException("Evaluator cannot be blank");
        }
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(evaluationStatus);
    }

    public boolean isHighRisk() {
        return "HIGH".equalsIgnoreCase(riskLevel);
    }
}