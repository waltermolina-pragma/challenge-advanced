package com.loanmanagement.domain.model;

public enum LoanStatus {
    PENDING_EVALUATION,
    APPROVED,
    APPROVED_WITH_CONDITIONS,
    REJECTED,
    REVIEW_REQUIRED,
    CANCELLED;

    public boolean isApproved() {
        return this == APPROVED || this == APPROVED_WITH_CONDITIONS;
    }
}
