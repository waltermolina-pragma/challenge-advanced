package com.loanmanagement.domain.ports;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import java.util.UUID;

public interface RiskEvaluationService {
    RiskEvaluation evaluateRisk(LoanApplication loanApplication);
    RiskEvaluation getEvaluationByLoanApplicationId(UUID loanApplicationId);
    boolean isHighRisk(UUID loanApplicationId);
    boolean isApproved(UUID loanApplicationId);
}