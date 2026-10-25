package com.loanmanagement.infrastructure.adapters;

import com.loanmanagement.domain.model.Customer;
import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.RiskEvaluation;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskRequest;
import com.loanmanagement.infrastructure.adapters.risk.dto.ExternalRiskResponse;

import com.loanmanagement.infrastructure.adapters.risk.mapper.ExternalRiskMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExternalRiskMapperTest {
    private final ExternalRiskMapper mapper = Mappers.getMapper(ExternalRiskMapper.class);

    @Test
    void shouldMapExternalRiskResponseToDomain() {
        UUID loanId = UUID.randomUUID();

        ExternalRiskResponse response = new ExternalRiskResponse(
                "APPROVED",
                750,
                List.of("Verificar documentación")
        );

        RiskEvaluation result = mapper.toDomain(response, loanId);

        assertNotNull(result.getId());
        assertEquals(loanId, result.getLoanApplicationId());
        assertEquals("APPROVED", result.getEvaluationStatus());
        assertEquals("LOW", result.getRiskLevel());
        assertEquals("ExternalRiskService", result.getEvaluator());
        assertNotNull(result.getEvaluationDate());
        assertTrue(result.getComments().contains("Verificar documentación"));
    }

    @Test
    void shouldMapLoanApplicationToExternalRiskRequest() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Juan Pérez",
                "12345678",
                LocalDate.now().minusYears(30),
                new BigDecimal("5000"),
                "EMPLOYED",
                "750"
        );

        LoanApplication application = new LoanApplication(
                UUID.randomUUID(),
                customer.getId(),
                new BigDecimal("30000"),
                36,
                "Vivienda"
        );

        application.setCustomer(customer);

        ExternalRiskRequest result = mapper.toRequest(application);

        assertEquals(customer.getId().toString(), result.customerId());
        assertEquals("12345678", result.identificationNumber());
        assertEquals(new BigDecimal("5000"), result.monthlyIncome());
        assertEquals(new BigDecimal("30000"), result.requestedAmount());
        assertEquals(36, result.termMonths());
        assertEquals("EMPLOYED", result.employmentStatus());
        assertEquals("750", result.creditScore());
    }

    @Test
    void shouldReturnNullWhenLoanApplicationIsNull() {
        assertNull(mapper.toRequest(null));
    }

    @Test
    void shouldRejectUnknownExternalRiskStatus() {
        ExternalRiskResponse response = new ExternalRiskResponse(
                "UNKNOWN",
                750,
                List.of()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toDomain(response, UUID.randomUUID())
        );
    }

    @Test
    void shouldMapLoanApplicationWithoutCustomer() {
        LoanApplication application = new LoanApplication(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );

        application.setCustomer(null);

        ExternalRiskRequest result = mapper.toRequest(application);

        assertNotNull(result);
        assertNull(result.customerId());
        assertNull(result.identificationNumber());
        assertNull(result.monthlyIncome());
        assertNull(result.employmentStatus());
        assertNull(result.creditScore());

        assertEquals(new BigDecimal("50000"), result.requestedAmount());
        assertEquals(36, result.termMonths());
    }

    @Test
    void shouldReturnNullWhenExternalRiskResponseAndLoanApplicationIdAreNull() {
        RiskEvaluation result = mapper.toDomain(null, null);

        assertNull(result);
    }

    @Test
    void shouldRejectRiskEvaluationWhenResponseIsNull() {
        UUID loanApplicationId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toDomain(null, loanApplicationId)
        );
    }

    @Test
    void shouldBuildCommentsWhenWarningsAreNull() {
        ExternalRiskResponse response = new ExternalRiskResponse(
                "APPROVED",
                750,
                null
        );

        RiskEvaluation result = mapper.toDomain(response, UUID.randomUUID());

        assertEquals("Puntuación de riesgo: 750", result.getComments());
    }

    @Test
    void shouldBuildCommentsWhenWarningsAreEmpty() {
        ExternalRiskResponse response = new ExternalRiskResponse(
                "APPROVED",
                750,
                List.of()
        );

        RiskEvaluation result = mapper.toDomain(response, UUID.randomUUID());

        assertEquals("Puntuación de riesgo: 750", result.getComments());
    }
}