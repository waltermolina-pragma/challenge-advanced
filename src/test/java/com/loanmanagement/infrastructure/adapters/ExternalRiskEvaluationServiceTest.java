package com.loanmanagement.infrastructure.adapters;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.infrastructure.adapters.risk.ExternalRiskEvaluationService;
import com.loanmanagement.infrastructure.adapters.risk.client.RiskEvaluationClient;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import com.loanmanagement.infrastructure.adapters.risk.mapper.ExternalRiskMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalRiskEvaluationServiceTest {

    @Mock
    private RiskEvaluationClient riskEvaluationClient;

    @Mock
    private ExternalRiskMapper externalRiskMapper;

    @InjectMocks
    private ExternalRiskEvaluationService service;

    @Test
    void shouldEvaluateLoanApplicationRisk() {
        UUID id = UUID.randomUUID();

        LoanApplication application = new LoanApplication(
                id,
                UUID.randomUUID(),
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );

        ExternalRiskRequest request = mock(ExternalRiskRequest.class);

        ExternalRiskResponse response = new ExternalRiskResponse(
                "APPROVED",
                750,
                List.of()
        );

        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setLoanApplicationId(id);
        evaluation.setEvaluationStatus("APPROVED");
        evaluation.setRiskLevel("LOW");

        when(externalRiskMapper.toRequest(application)).thenReturn(request);
        when(riskEvaluationClient.evaluate(request)).thenReturn(response);
        when(externalRiskMapper.toDomain(response, id)).thenReturn(evaluation);

        RiskEvaluation result = service.evaluateRisk(application);

        assertSame(evaluation, result);

        verify(externalRiskMapper).toRequest(application);
        verify(riskEvaluationClient).evaluate(request);
        verify(externalRiskMapper).toDomain(response, id);
    }
}