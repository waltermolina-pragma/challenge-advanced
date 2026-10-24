package com.loanmanagement.infrastructure.adapters.risk.client;

import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockRiskEvaluationClient implements RiskEvaluationClient {
    @Override
    public ExternalRiskResponse evaluate(ExternalRiskRequest request) {
        return new ExternalRiskResponse("APPROVED", 10, List.of());
    }
}
