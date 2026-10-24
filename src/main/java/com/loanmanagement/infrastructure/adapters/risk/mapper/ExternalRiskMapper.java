package com.loanmanagement.infrastructure.adapters.risk.mapper;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.model.RiskEvaluationStatus;
import com.loanmanagement.domain.model.RiskLevel;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ExternalRiskMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "loanApplicationId", source = "loanApplicationId")
    @Mapping(target = "evaluationStatus", source = "response.status", qualifiedByName = "mapEvaluationStatus")
    @Mapping(target = "riskLevel", source = "response.riskScore", qualifiedByName = "mapRiskLevel")
    @Mapping(target = "evaluator", constant = "ExternalRiskService")
    @Mapping(target = "evaluationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "comments", source = "response", qualifiedByName = "buildComments")
    RiskEvaluation toDomain(
            ExternalRiskResponse response,
            UUID loanApplicationId
    );

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "identificationNumber", source = "customer.identificationNumber")
    @Mapping(target = "monthlyIncome", source = "customer.monthlyIncome")
    @Mapping(target = "requestedAmount", source = "requestedAmount")
    @Mapping(target = "termMonths", source = "termMonths")
    @Mapping(target = "employmentStatus", source = "customer.employmentStatus")
    @Mapping(target = "creditScore", source = "customer.creditScore")
    ExternalRiskRequest toRequest(LoanApplication application);

    @Named("mapEvaluationStatus")
    default String mapEvaluationStatus(String status) {
        return RiskEvaluationStatus.fromExternal(status).name();
    }

    @Named("mapRiskLevel")
    default String mapRiskLevel(Integer score) {
        return RiskLevel.fromScore(score).name();
    }

    @Named("buildComments")
    default String buildComments(ExternalRiskResponse response) {
        String comments = "Puntuación de riesgo: " + response.riskScore();
        if (response.warnings() != null && !response.warnings().isEmpty()) {
            comments += ". Advertencias: " + String.join(", ", response.warnings());
        }
        return comments;
    }
}