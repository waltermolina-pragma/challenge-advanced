package com.loanmanagement.domain.ports;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;

public interface RiskEvaluationService {
    RiskEvaluation evaluateRisk(LoanApplication loanApplication);
}