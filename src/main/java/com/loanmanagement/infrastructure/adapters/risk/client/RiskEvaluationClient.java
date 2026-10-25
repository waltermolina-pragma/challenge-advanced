package com.loanmanagement.infrastructure.adapters.risk.client;

import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;

public interface RiskEvaluationClient {
    ExternalRiskResponse evaluate(ExternalRiskRequest request);
}
