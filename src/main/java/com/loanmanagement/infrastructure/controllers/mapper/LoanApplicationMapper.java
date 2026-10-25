package com.loanmanagement.infrastructure.controllers.mapper;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface LoanApplicationMapper {
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "customerIdentification", source = "customer.identificationNumber")
    @Mapping(target = "riskEvaluationStatus", source = "riskEvaluation.evaluationStatus")
    @Mapping(target = "riskLevel", source = "riskEvaluation.riskLevel")
    @Mapping(target = "evaluator", source = "riskEvaluation.evaluator")
    @Mapping(target = "evaluationDate", source = "riskEvaluation.evaluationDate")
    @Mapping(target = "evaluationComments", source = "riskEvaluation.comments")
    @Mapping(target = "applicationDate", source = "applicationDate", dateFormat = "yyyy-MM-dd")
    LoanApplicationResponse toResponse(LoanApplication loanApplication);

    List<LoanApplicationResponse> toResponseList(List<LoanApplication> loanApplications);
}
