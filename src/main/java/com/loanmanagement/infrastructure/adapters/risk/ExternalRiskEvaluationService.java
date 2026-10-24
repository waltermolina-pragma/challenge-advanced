package com.loanmanagement.infrastructure.adapters.risk;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.ports.RiskEvaluationService;
import com.loanmanagement.infrastructure.adapters.risk.client.RiskEvaluationClient;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import com.loanmanagement.infrastructure.adapters.risk.mapper.ExternalRiskMapper;
import org.springframework.stereotype.Service;

@Service
public class ExternalRiskEvaluationService implements RiskEvaluationService {
    private final RiskEvaluationClient riskEvaluationClient;
    private final ExternalRiskMapper externalRiskMapper;

    public ExternalRiskEvaluationService(RiskEvaluationClient riskEvaluationClient, ExternalRiskMapper externalRiskMapper) {
        this.riskEvaluationClient = riskEvaluationClient;
        this.externalRiskMapper = externalRiskMapper;
    }

    @Override
    public RiskEvaluation evaluateRisk(LoanApplication application) {
        ExternalRiskRequest request = externalRiskMapper.toRequest(application);
        ExternalRiskResponse response = riskEvaluationClient.evaluate(request);
        return externalRiskMapper.toDomain(response, application.getId());
    }
}