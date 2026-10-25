package com.loanmanagement.model;

import com.loanmanagement.domain.model.LoanStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanStatusTest {

    @Test
    void shouldIdentifyApprovedLoanStatuses() {
        assertTrue(LoanStatus.APPROVED.isApproved());
        assertTrue(LoanStatus.APPROVED_WITH_CONDITIONS.isApproved());
    }

    @Test
    void shouldIdentifyNonApprovedLoanStatuses() {
        assertFalse(LoanStatus.PENDING_EVALUATION.isApproved());
        assertFalse(LoanStatus.REJECTED.isApproved());
        assertFalse(LoanStatus.REVIEW_REQUIRED.isApproved());
        assertFalse(LoanStatus.CANCELLED.isApproved());
    }
}