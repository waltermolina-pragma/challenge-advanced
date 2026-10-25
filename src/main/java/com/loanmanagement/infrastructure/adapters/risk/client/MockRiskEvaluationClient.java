package com.loanmanagement.infrastructure.adapters.risk.client;

import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.loanmanagement.domain.model.RiskEvaluationStatus.REQUIRES_REVIEW;

@Component
public class MockRiskEvaluationClient implements RiskEvaluationClient {
    @Override
    public ExternalRiskResponse evaluate(ExternalRiskRequest request) {
        return new ExternalRiskResponse(REQUIRES_REVIEW.name(), 500, List.of());
    }
}
