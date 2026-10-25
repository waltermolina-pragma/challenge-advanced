package com.loanmanagement.application.usecases;

import com.loanmanagement.domain.model.Customer;
import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.ports.LoanApplicationRepository;
import com.loanmanagement.domain.ports.RiskEvaluationService;
import com.loanmanagement.infrastructure.config.exception.ResourceNotFoundException;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final RiskEvaluationService riskEvaluationService;

    public LoanApplication createLoanApplication(LoanApplicationRequest request) {
        LoanApplication domain = new LoanApplication(
                UUID.randomUUID(),
                request.customerId(),
                request.requestedAmount(),
                request.loanTerm(),
                request.purpose()
        );
        domain.setCollateralType(request.collateralType());
        domain.setCollateralValue(request.collateralValue());
        domain.setAdditionalNotes(request.additionalNotes());
        domain.validateApplication();
        return loanApplicationRepository.save(domain);
    }

    public List<LoanApplication> findAll() {
        return loanApplicationRepository.findAll();
    }

    public void deleteById(UUID id) {
        deleteApplication(id);
    }

    public LoanApplication findById(UUID id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de préstamo no encontrada: " + id));
    }

    public List<LoanApplication> findByCustomerId(UUID customerId) {
        return loanApplicationRepository.findByCustomerId(customerId);
    }

    public List<LoanApplication> findByStatus(String status) {
        return loanApplicationRepository.findByStatus(status);
    }

    public LoanApplication evaluateAndApprove(UUID applicationId) {
        LoanApplication application = findById(applicationId);
        if (!"PENDING_EVALUATION".equals(application.getStatus())) {
            throw new IllegalStateException("La solicitud no está en estado de evaluación: " + application.getStatus());
        }

        Customer customer = application.getCustomer();
        if (customer != null && !customer.isEligibleForLoan(application.getRequestedAmount())) {
            application.setStatus("REJECTED");
            application.setRejectionReason("Cliente no cumple requisitos de elegibilidad");
            return loanApplicationRepository.save(application);
        }

        RiskEvaluation evaluation = riskEvaluationService.evaluateRisk(application);
        application.setRiskEvaluation(evaluation);

        if (evaluation.isApproved() && !evaluation.isHighRisk()) {
            application.setStatus("APPROVED");
            application.setApprovedAmount(application.getRequestedAmount());
            application.setApprovedTerm(application.getTermMonths());
            application.setApprovalDate(LocalDateTime.now());
        } else if (evaluation.isHighRisk()) {
            application.setStatus("REJECTED");
            application.setRejectionReason("Riesgo demasiado alto según evaluación externa");
        } else {
            application.setStatus("REVIEW_REQUIRED");
            application.setRejectionReason("Evaluación de riesgo requiere revisión manual");
        }

        return loanApplicationRepository.save(application);
    }

    public LoanApplication approveWithConditions(UUID applicationId, BigDecimal approvedAmount, String conditions) {
        LoanApplication application = findById(applicationId);

        if (!"REVIEW_REQUIRED".equals(application.getStatus())) {
            throw new IllegalStateException(
                    "La solicitud no está en estado de revisión: " + application.getStatus());
        }

        if (approvedAmount.compareTo(application.getRequestedAmount()) > 0) {
            throw new IllegalArgumentException(
                    "El monto aprobado no puede exceder el solicitado");
        }

        application.setStatus("APPROVED_WITH_CONDITIONS");
        application.setApprovedAmount(approvedAmount);
        application.setApplicationDate(LocalDateTime.now());
        application.setApprovalConditions(conditions);

        return loanApplicationRepository.save(application);
    }

    public void cancelApplication(UUID applicationId) {
        LoanApplication application = findById(applicationId);
        String currentStatus = application.getStatus();
        if ("APPROVED".equals(currentStatus) || "APPROVED_WITH_CONDITIONS".equals(currentStatus)) {
            throw new IllegalStateException("No se puede cancelar una solicitud ya aprobada");
        }
        loanApplicationRepository.updateStatus(applicationId, "CANCELLED");
    }

    public void deleteApplication(UUID applicationId) {
        LoanApplication application = findById(applicationId);
        String currentStatus = application.getStatus();
        if ("APPROVED".equals(currentStatus) || "APPROVED_WITH_CONDITIONS".equals(currentStatus)) {
            throw new IllegalStateException("No se puede eliminar una solicitud aprobada. Cancele primero.");
        }
        loanApplicationRepository.deleteById(applicationId);
    }
}