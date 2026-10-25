package com.loanmanagement.application.usecases;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.domain.ports.LoanApplicationRepository;
import com.loanmanagement.domain.ports.RiskEvaluationService;
import com.loanmanagement.infrastructure.config.exception.ResourceNotFoundException;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.loanmanagement.domain.model.LoanStatus.*;
import static com.loanmanagement.domain.model.RiskLevel.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private RiskEvaluationService riskService;

    @InjectMocks
    private LoanApplicationService service;

    private UUID id;
    private UUID customerId;
    private LoanApplication application;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        customerId = UUID.randomUUID();
        application = new LoanApplication(
                id,
                customerId,
                new BigDecimal("50000"),
                36,
                "Compra de vivienda"
        );
    }

    @Test
    void shouldCreateLoanApplication() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                customerId,
                new BigDecimal("50000"),
                36,
                "Compra de vivienda",
                null,
                "PROPERTY",
                new BigDecimal("75000"),
                "Observaciones"
        );

        when(repository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanApplication result = service.createLoanApplication(request);

        assertEquals(customerId, result.getCustomerId());
        assertEquals(PENDING_EVALUATION.name(), result.getStatus());
        assertEquals("PROPERTY", result.getCollateralType());
        assertEquals("Observaciones", result.getAdditionalNotes());

        verify(repository).save(any(LoanApplication.class));
    }

    @Test
    void shouldRejectInvalidAmountWhenCreatingLoanApplication() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                customerId,
                BigDecimal.ZERO,
                36,
                "Vivienda",
                null,
                null,
                null,
                null
        );

        assertThrows(IllegalArgumentException.class, () -> service.createLoanApplication(request));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnLoanApplicationById() {
        when(repository.findById(id)).thenReturn(Optional.of(application));

        LoanApplication result = service.findById(id);

        assertSame(application, result);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFindingLoanApplicationById() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(id)
        );
    }

    @Test
    void shouldReturnAllLoanApplications() {
        when(repository.findAll()).thenReturn(List.of(application));

        assertEquals(
                List.of(application),
                service.findAll()
        );
    }

    @Test
    void shouldReturnLoanApplicationByCustomerId() {
        when(repository.findByCustomerId(customerId)).thenReturn(List.of(application));

        assertEquals(
                List.of(application),
                service.findByCustomerId(customerId)
        );
    }

    @Test
    void shouldReturnLoanApplicationByStatus() {
        when(repository.findByStatus(PENDING_EVALUATION.name())).thenReturn(List.of(application));

        assertEquals(
                1,
                service.findByStatus(PENDING_EVALUATION.name()).size()
        );
    }

    @Test
    void shouldApproveLoanApplication() {
        RiskEvaluation risk = risk(APPROVED.name(), LOW.name());

        when(repository.findById(id)).thenReturn(Optional.of(application));
        when(riskService.evaluateRisk(application)).thenReturn(risk);
        when(repository.save(application)).thenReturn(application);

        LoanApplication result = service.evaluateAndApprove(id);

        assertEquals(APPROVED.name(), result.getStatus());
        assertSame(risk, result.getRiskEvaluation());

        assertEquals(0,
                result.getApprovedAmount().compareTo(new BigDecimal("50000"))
        );

        assertEquals(36, result.getApprovedTerm());
        assertNotNull(result.getApprovalDate());
        verify(repository).save(application);
    }

    @Test
    void shouldRejectApprovalOfLoanApplicationWhenRiskIsHigh() {
        when(repository.findById(id)).thenReturn(Optional.of(application));
        when(riskService.evaluateRisk(application)).thenReturn(risk(APPROVED.name(), HIGH.name()));
        when(repository.save(application)).thenReturn(application);

        LoanApplication result = service.evaluateAndApprove(id);

        assertEquals(REJECTED.name(), result.getStatus());
        assertNotNull(result.getRejectionReason());

        verify(repository).save(application);
    }

    @Test
    void shouldEvaluateAndBecauseOfConditionsReturnReview() {
        when(repository.findById(id)).thenReturn(Optional.of(application));
        when(riskService.evaluateRisk(application)).thenReturn(risk(REVIEW_REQUIRED.name(), MEDIUM.name()));
        when(repository.save(application)).thenReturn(application);

        LoanApplication result = service.evaluateAndApprove(id);

        assertEquals(REVIEW_REQUIRED.name(), result.getStatus());
    }

    @Test
    void shouldReturnExceptionBecauseOfStatus() {
        application.setStatus(APPROVED.name());
        when(repository.findById(id)).thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> service.evaluateAndApprove(id)
        );

        verifyNoInteractions(riskService);
    }

    @Test
    void shouldApproveWithConditions() {
        application.setStatus(REVIEW_REQUIRED.name());

        when(repository.findById(id)).thenReturn(Optional.of(application));
        when(repository.save(application)).thenReturn(application);

        LoanApplication result = service.approveWithConditions(
                id,
                new BigDecimal("45000"),
                "Presentar aval"
        );

        assertEquals(APPROVED_WITH_CONDITIONS.name(), result.getStatus());
        assertEquals(
                new BigDecimal("45000"),
                result.getApprovedAmount()
        );
        assertEquals(
                "Presentar aval",
                result.getApprovalConditions()
        );

        verify(repository).save(application);
    }

    @Test
    void shouldRejectApprovalWithConditionsWhenAmountExceeds() {
        application.setStatus(REVIEW_REQUIRED.name());

        when(repository.findById(id)).thenReturn(Optional.of(application));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.approveWithConditions(
                        id,
                        new BigDecimal("60000"),
                        "Aval"
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectApprovalWithConditionsWhenStatusIsWrong() {
        when(repository.findById(id)).thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> service.approveWithConditions(
                        id,
                        new BigDecimal("40000"),
                        "Aval"
                )
        );
    }

    @Test
    void shouldCancelLoanApplication() {
        when(repository.findById(id)).thenReturn(Optional.of(application));

        service.cancelApplication(id);

        verify(repository).updateStatus(id, CANCELLED.name());
    }

    @Test
    void shouldRejectCancelBecauseOfStatus() {
        application.setStatus(APPROVED.name());

        when(repository.findById(id)).thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> service.cancelApplication(id)
        );

        verify(repository, never()).updateStatus(any(), anyString());
    }

    @Test
    void shouldDeleteLoanApplication() {
        when(repository.findById(id)).thenReturn(Optional.of(application));

        service.deleteById(id);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldRejectDeleteBecauseOfStatus() {
        application.setStatus(APPROVED.name());

        when(repository.findById(id)).thenReturn(Optional.of(application));

        assertThrows(
                IllegalStateException.class,
                () -> service.deleteById(id)
        );

        verify(repository, never()).deleteById(any());
    }

    private RiskEvaluation risk(String status, String level) {
        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setLoanApplicationId(id);
        evaluation.setEvaluationStatus(status);
        evaluation.setRiskLevel(level);
        evaluation.setEvaluator("MockRisk");
        evaluation.setEvaluationDate(LocalDateTime.now());
        evaluation.setComments("");
        evaluation.validate();
        return evaluation;
    }
}