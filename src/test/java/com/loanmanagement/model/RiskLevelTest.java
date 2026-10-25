package com.loanmanagement.model;

import com.loanmanagement.domain.model.RiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskLevelTest {

    @Test
    void shouldReturnLowRiskWhenScoreIsAtLeast700() {
        assertEquals(RiskLevel.LOW, RiskLevel.fromScore(700));
        assertEquals(RiskLevel.LOW, RiskLevel.fromScore(850));
    }

    @Test
    void shouldReturnMediumRiskWhenScoreIsBetween400And699() {
        assertEquals(RiskLevel.MEDIUM, RiskLevel.fromScore(400));
        assertEquals(RiskLevel.MEDIUM, RiskLevel.fromScore(699));
    }

    @Test
    void shouldReturnHighRiskWhenScoreIsBelow400() {
        assertEquals(RiskLevel.HIGH, RiskLevel.fromScore(399));
        assertEquals(RiskLevel.HIGH, RiskLevel.fromScore(100));
    }

    @Test
    void shouldRejectNullRiskScore() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RiskLevel.fromScore(null)
        );
    }
}