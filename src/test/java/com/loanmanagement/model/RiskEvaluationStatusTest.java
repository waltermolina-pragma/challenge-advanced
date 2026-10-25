package com.loanmanagement.model;

import com.loanmanagement.domain.model.RiskEvaluationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskEvaluationStatusTest {

    @Test
    void shouldConvertApprovedExternalStatus() {
        assertEquals(
                RiskEvaluationStatus.APPROVED,
                RiskEvaluationStatus.fromExternal("approved")
        );
    }

    @Test
    void shouldConvertRejectedExternalStatus() {
        assertEquals(
                RiskEvaluationStatus.REJECTED,
                RiskEvaluationStatus.fromExternal("REJECTED")
        );
    }

    @Test
    void shouldConvertRequiresReviewExternalStatus() {
        assertEquals(
                RiskEvaluationStatus.REQUIRES_REVIEW,
                RiskEvaluationStatus.fromExternal(" requires_review ")
        );
    }

    @Test
    void shouldRejectNullOrBlankExternalStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RiskEvaluationStatus.fromExternal(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> RiskEvaluationStatus.fromExternal(" ")
        );
    }

    @Test
    void shouldRejectUnknownExternalStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RiskEvaluationStatus.fromExternal("UNKNOWN")
        );
    }
}