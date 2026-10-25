package com.loanmanagement.model;

import com.loanmanagement.domain.model.RiskEvaluation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RiskEvaluationTest {

    private RiskEvaluation evaluation;

    @BeforeEach
    void setUp() {
        evaluation = new RiskEvaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setLoanApplicationId(UUID.randomUUID());
        evaluation.setEvaluationStatus("APPROVED");
        evaluation.setRiskLevel("LOW");
        evaluation.setEvaluator("ExternalRiskService");
        evaluation.setEvaluationDate(LocalDateTime.now());
        evaluation.setComments("Evaluación correcta");
    }

    @Test
    void shouldValidateRiskEvaluation() {
        assertDoesNotThrow(evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutId() {
        evaluation.setId(null);

        assertThrows(NullPointerException.class, evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutLoanApplicationId() {
        evaluation.setLoanApplicationId(null);

        assertThrows(NullPointerException.class, evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutDate() {
        evaluation.setEvaluationDate(null);

        assertThrows(NullPointerException.class, evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutStatus() {
        evaluation.setEvaluationStatus(null);

        assertThrows(IllegalArgumentException.class, evaluation::validate);

        evaluation.setEvaluationStatus(" ");

        assertThrows(IllegalArgumentException.class, evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutRiskLevel() {
        evaluation.setRiskLevel(null);

        assertThrows(IllegalArgumentException.class, evaluation::validate);

        evaluation.setRiskLevel(" ");

        assertThrows(IllegalArgumentException.class, evaluation::validate);
    }

    @Test
    void shouldRejectRiskEvaluationWithoutEvaluator() {
        evaluation.setEvaluator(null);

        assertThrows(IllegalArgumentException.class, evaluation::validate);

        evaluation.setEvaluator(" ");

        assertThrows(IllegalArgumentException.class, evaluation::validate);
    }

    @Test
    void shouldIdentifyApprovedEvaluation() {
        assertTrue(evaluation.isApproved());

        evaluation.setEvaluationStatus("approved");

        assertTrue(evaluation.isApproved());

        evaluation.setEvaluationStatus("REJECTED");

        assertFalse(evaluation.isApproved());
    }

    @Test
    void shouldIdentifyHighRiskEvaluation() {
        evaluation.setRiskLevel("HIGH");

        assertTrue(evaluation.isHighRisk());

        evaluation.setRiskLevel("high");

        assertTrue(evaluation.isHighRisk());

        evaluation.setRiskLevel("LOW");

        assertFalse(evaluation.isHighRisk());
    }
}