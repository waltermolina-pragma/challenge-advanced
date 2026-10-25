package com.loanmanagement.model;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.model.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationTest {

    private LoanApplication application;

    @BeforeEach
    void setUp() {
        application = new LoanApplication(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("50000"),
                36,
                "Compra de vivienda"
        );
    }

    @Test
    void shouldCreateLoanApplicationWithInitialStatus() {
        assertEquals(LoanStatus.PENDING_EVALUATION.name(), application.getStatus());
        assertNotNull(application.getApplicationDate());
        assertNotNull(application.getLastUpdateDate());
        assertEquals(36, application.getTermMonths());
    }

    @Test
    void shouldValidateLoanApplication() {
        assertDoesNotThrow(application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWithoutCustomerId() {
        application.setCustomerId(null);

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWithoutRequestedAmount() {
        application.setRequestedAmount(null);

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWithNonPositiveAmount() {
        application.setRequestedAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, application::validateApplication);

        application.setRequestedAmount(new BigDecimal("-100"));

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWhenAmountExceedsMaximum() {
        application.setRequestedAmount(new BigDecimal("1000001"));

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldAllowMaximumRequestedAmount() {
        application.setRequestedAmount(new BigDecimal("1000000"));

        assertDoesNotThrow(application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWithoutTerm() {
        application.setTermMonths(null);

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWithInvalidTerm() {
        application.setTermMonths(0);

        assertThrows(IllegalArgumentException.class, application::validateApplication);

        application.setTermMonths(-1);

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldRejectLoanApplicationWhenTermExceedsMaximum() {
        application.setTermMonths(361);

        assertThrows(IllegalArgumentException.class, application::validateApplication);
    }

    @Test
    void shouldAllowMaximumLoanTerm() {
        application.setTermMonths(360);

        assertDoesNotThrow(application::validateApplication);
    }

    @Test
    void shouldUpdateLastUpdateDateWhenStatusChanges() {
        LocalDateTime previousDate = application.getLastUpdateDate();

        application.setStatus(LoanStatus.APPROVED.name());

        assertEquals(LoanStatus.APPROVED.name(), application.getStatus());
        assertFalse(application.getLastUpdateDate().isBefore(previousDate));
    }

    @Test
    void shouldAllowNoArgsConstructor() {
        LoanApplication emptyApplication = new LoanApplication();

        assertNull(emptyApplication.getId());
        assertNull(emptyApplication.getStatus());
    }
}