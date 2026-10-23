package com.loanmanagement.infrastructure.adapters;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.ports.RiskEvaluationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ExternalRiskEvaluationService implements RiskEvaluationService {

    private static final String EXTERNAL_EVALUATOR = "ExternalRiskService";
    private static final String APPROVED_STATUS = "APPROVED";
    private static final String REJECTED_STATUS = "REJECTED";
    private static final String REQUIRES_REVIEW_STATUS = "REQUIRES_REVIEW";
    private static final String HIGH_RISK_LEVEL = "HIGH";
    private static final String MEDIUM_RISK_LEVEL = "MEDIUM";
    private static final String LOW_RISK_LEVEL = "LOW";

    private final RiskEvaluationClient riskEvaluationClient;

    public ExternalRiskEvaluationService(RiskEvaluationClient riskEvaluationClient) {
        this.riskEvaluationClient = riskEvaluationClient;
    }

    @Override
    public RiskEvaluation evaluateRisk(LoanApplication application) {
        ExternalRiskData request = buildRiskRequest(application);
        
        ExternalRiskResponse response = riskEvaluationClient.evaluate(request);
        
        return mapToRiskEvaluation(application.getId(), response);
    }

    @Override
    public RiskEvaluation getEvaluationByLoanApplicationId(UUID loanApplicationId) {
        return null;
    }

    @Override
    public boolean isHighRisk(UUID loanApplicationId) {
        return false;
    }

    @Override
    public boolean isApproved(UUID loanApplicationId) {
        return false;
    }

    private ExternalRiskData buildRiskRequest(LoanApplication application) {
        ExternalRiskData data = new ExternalRiskData();
        if (application.getCustomer() != null) {
            if (application.getCustomer().getId() != null) {
                data.setCustomerId(application.getCustomer().getId().toString());
            }
            data.setIdentificationNumber(application.getCustomer().getIdentificationNumber());
            data.setMonthlyIncome(application.getCustomer().getMonthlyIncome());
            data.setEmploymentStatus(application.getCustomer().getEmploymentStatus());
            data.setCreditScore(application.getCustomer().getCreditScore());
        }
        data.setRequestedAmount(application.getRequestedAmount());
        data.setTermMonths(application.getTermMonths());
        return data;
    }

    private RiskEvaluation mapToRiskEvaluation(UUID applicationId, ExternalRiskResponse response) {
        String evaluationStatus = mapStatus(response.getStatus());
        String riskLevel = mapRiskLevel(response.getRiskScore());
        String comments = buildComments(response);

        return new RiskEvaluation(
                UUID.randomUUID(),
                applicationId,
                evaluationStatus,
                riskLevel,
                EXTERNAL_EVALUATOR,
                LocalDateTime.now(),
                comments
        );
    }

    private String mapStatus(String externalStatus) {
        if ("APPROVED".equalsIgnoreCase(externalStatus)) {
            return APPROVED_STATUS;
        } else if ("REJECTED".equalsIgnoreCase(externalStatus)) {
            return REJECTED_STATUS;
        } else {
            return REQUIRES_REVIEW_STATUS;
        }
    }

    private String mapRiskLevel(Integer riskScore) {
        if (riskScore == null) {
            return MEDIUM_RISK_LEVEL;
        }
        if (riskScore >= 700) {
            return LOW_RISK_LEVEL;
        } else if (riskScore >= 400) {
            return MEDIUM_RISK_LEVEL;
        } else {
            return HIGH_RISK_LEVEL;
        }
    }

    private String buildComments(ExternalRiskResponse response) {
        StringBuilder comments = new StringBuilder();
        comments.append("Puntuación de riesgo: ").append(response.getRiskScore()).append(". ");
        if (response.getWarnings() != null && !response.getWarnings().isEmpty()) {
            comments.append("Advertencias: ").append(String.join(", ", response.getWarnings()));
        }
        return comments.toString();
    }

    public static class ExternalRiskData {
        private String customerId;
        private String identificationNumber;
        private java.math.BigDecimal monthlyIncome;
        private java.math.BigDecimal requestedAmount;
        private Integer termMonths;
        private String employmentStatus;
        private String creditScore;

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public String getIdentificationNumber() { return identificationNumber; }
        public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }
        public java.math.BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(java.math.BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
        public java.math.BigDecimal getRequestedAmount() { return requestedAmount; }
        public void setRequestedAmount(java.math.BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
        public Integer getTermMonths() { return termMonths; }
        public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
        public String getEmploymentStatus() { return employmentStatus; }
        public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
        public String getCreditScore() { return creditScore; }
        public void setCreditScore(String creditScore) { this.creditScore = creditScore; }
    }

    public static class ExternalRiskResponse {
        private String status;
        private Integer riskScore;
        private java.util.List<String> warnings;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getRiskScore() { return riskScore; }
        public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
        public java.util.List<String> getWarnings() { return warnings; }
        public void setWarnings(java.util.List<String> warnings) { this.warnings = warnings; }
    }
}

@org.springframework.stereotype.Component
class MockRiskEvaluationClient implements RiskEvaluationClient {
    @Override
    public ExternalRiskEvaluationService.ExternalRiskResponse evaluate(ExternalRiskEvaluationService.ExternalRiskData request) {
        ExternalRiskEvaluationService.ExternalRiskResponse res = new ExternalRiskEvaluationService.ExternalRiskResponse();
        res.setStatus("APPROVED");
        res.setRiskScore(750);
        res.setWarnings(java.util.Collections.emptyList());
        return res;
    }
}

interface RiskEvaluationClient {
    ExternalRiskEvaluationService.ExternalRiskResponse evaluate(
            ExternalRiskEvaluationService.ExternalRiskData request);
}