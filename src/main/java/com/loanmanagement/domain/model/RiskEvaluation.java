package com.loanmanagement.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class RiskEvaluation {
    private final UUID id;
    private final UUID loanApplicationId;
    private final String evaluationStatus;
    private final String riskLevel;
    private final String evaluator;
    private final LocalDateTime evaluationDate;
    private final String comments;

    public RiskEvaluation(UUID id, UUID loanApplicationId, String evaluationStatus,
                          String riskLevel, String evaluator, LocalDateTime evaluationDate,
                          String comments) {
        this.id = Objects.requireNonNull(id, "Risk evaluation ID cannot be null");
        this.loanApplicationId = Objects.requireNonNull(loanApplicationId, "Loan application ID cannot be null");
        this.evaluationStatus = Objects.requireNonNull(evaluationStatus, "Evaluation status cannot be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "Risk level cannot be null");
        this.evaluator = Objects.requireNonNull(evaluator, "Evaluator cannot be null");
        this.evaluationDate = Objects.requireNonNull(evaluationDate, "Evaluation date cannot be null");
        this.comments = comments != null ? comments : "";

        if (evaluationStatus.isBlank()) {
            throw new IllegalArgumentException("Evaluation status cannot be blank");
        }
        if (riskLevel.isBlank()) {
            throw new IllegalArgumentException("Risk level cannot be blank");
        }
        if (evaluator.isBlank()) {
            throw new IllegalArgumentException("Evaluator cannot be blank");
        }
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(evaluationStatus);
    }

    public boolean isHighRisk() {
        return "HIGH".equalsIgnoreCase(riskLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskEvaluation that = (RiskEvaluation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}