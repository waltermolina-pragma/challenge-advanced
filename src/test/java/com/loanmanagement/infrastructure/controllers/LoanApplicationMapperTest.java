package com.loanmanagement.infrastructure.controllers;

import com.loanmanagement.domain.model.Customer;
import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationResponse;

import com.loanmanagement.infrastructure.controllers.mapper.LoanApplicationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.loanmanagement.domain.model.LoanStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanApplicationMapperTest {
    private final LoanApplicationMapper mapper = Mappers.getMapper(LoanApplicationMapper.class);

    @Test
    void shouldMapLoanApplicationToResponse() {
        UUID id = UUID.randomUUID();

        LoanApplication application = new LoanApplication(
                id,
                UUID.randomUUID(),
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );

        LoanApplicationResponse response = mapper.toResponse(application);

        assertEquals(id, response.id());
        assertEquals(PENDING_EVALUATION.name(), response.status());
        assertEquals(new BigDecimal("50000"), response.requestedAmount());
        assertEquals("Vivienda", response.purpose());
    }

    @Test
    void shouldMapLoanApplicationsToResponseList() {
        LoanApplication application = new LoanApplication(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );

        List<LoanApplicationResponse> result =
                mapper.toResponseList(List.of(application));

        assertEquals(1, result.size());
        assertEquals(application.getId(), result.getFirst().id());
    }

    @Test
    void shouldReturnNullWhenLoanApplicationIsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void shouldReturnNullWhenLoanApplicationListIsNull() {
        assertNull(mapper.toResponseList(null));
    }

    @Test
    void shouldMapCustomerAndRiskEvaluationToResponse() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        LoanApplication application = new LoanApplication(
                id,
                customerId,
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );

        Customer customer = mock(Customer.class);
        when(customer.getName()).thenReturn("Juan Pérez");
        when(customer.getIdentificationNumber()).thenReturn("12345678");

        application.setCustomer(customer);

        LocalDateTime evaluationDate = LocalDateTime.now();

        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setLoanApplicationId(id);
        evaluation.setEvaluationStatus("APPROVED");
        evaluation.setRiskLevel("LOW");
        evaluation.setEvaluator("ExternalRiskService");
        evaluation.setEvaluationDate(evaluationDate);
        evaluation.setComments("Cliente apto para préstamo");

        application.setRiskEvaluation(evaluation);

        LoanApplicationResponse response = mapper.toResponse(application);

        assertNotNull(response);
        assertEquals(id, response.id());

        assertEquals("Juan Pérez", response.customerName());
        assertEquals("12345678", response.customerIdentification());

        assertEquals("APPROVED", response.riskEvaluationStatus());
        assertEquals("LOW", response.riskLevel());
        assertEquals("ExternalRiskService", response.evaluator());
        assertEquals(evaluationDate, response.evaluationDate());
        assertEquals("Cliente apto para préstamo", response.evaluationComments());
    }
}