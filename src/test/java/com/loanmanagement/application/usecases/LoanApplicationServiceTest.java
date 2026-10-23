package com.loanmanagement.application.usecases;

import com.loanmanagement.domain.ports.RiskEvaluationService;
import com.loanmanagement.domain.ports.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private com.loanmanagement.domain.ports.LoanApplicationRepository loanApplicationRepository;

    @Mock
    private com.loanmanagement.domain.ports.RiskEvaluationService riskEvaluationService;

    private LoanApplicationService loanApplicationService;

    @BeforeEach
    void setUp() {
        loanApplicationService = new LoanApplicationService(
            loanApplicationRepository,
            riskEvaluationService
        );
    }

    @Test
    void createLoanApplication_shouldCreateApplication() {
        fail("Test no implementado");
    }

    @Test
    void findById_shouldReturnApplication_whenExists() {
        fail("Test no implementado");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        fail("Test no implementado");
    }

    @Test
    void findByCustomerId_shouldReturnApplicationsForCustomer() {
        fail("Test no implementado");
    }

    @Test
    void findByStatus_shouldReturnApplicationsWithStatus() {
        fail("Test no implementado");
    }

    @Test
    void updateStatus_shouldUpdateStatus_whenApplicationExists() {
        fail("Test no implementado");
    }

    @Test
    void deleteById_shouldDelete_whenApplicationExists() {
        fail("Test no implementado");
    }

    @Test
    void evaluateRisk_shouldCallRiskEvaluationService_whenApplicationIsCreated() {
        fail("Test no implementado");
    }
}