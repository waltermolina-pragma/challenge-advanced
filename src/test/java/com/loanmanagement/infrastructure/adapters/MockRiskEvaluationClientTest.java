package com.loanmanagement.infrastructure.adapters;

import com.loanmanagement.infrastructure.adapters.risk.client.MockRiskEvaluationClient;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.loanmanagement.domain.model.RiskEvaluationStatus.REQUIRES_REVIEW;
import static org.junit.jupiter.api.Assertions.*;

class MockRiskEvaluationClientTest {
    private final MockRiskEvaluationClient client = new MockRiskEvaluationClient();

    @Test
    void shouldReturnMockRiskEvaluation() {
        ExternalRiskRequest request = new ExternalRiskRequest(
                "123",
                "12345678",
                new BigDecimal("5000"),
                new BigDecimal("30000"),
                36,
                "EMPLOYED",
                "750"
        );

        ExternalRiskResponse response = client.evaluate(request);

        assertNotNull(response);
        assertEquals(REQUIRES_REVIEW.name(), response.status());
        assertEquals(500, response.riskScore());
        assertTrue(response.warnings().isEmpty());
    }
}