package com.loanmanagement.infrastructure;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.infrastructure.adapters.loan.JpaLoanApplicationRepository;
import com.loanmanagement.infrastructure.repositories.LoanApplicationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.loanmanagement.domain.model.LoanStatus.CANCELLED;
import static com.loanmanagement.domain.model.LoanStatus.PENDING_EVALUATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaLoanApplicationRepositoryTest {

    @Mock
    private LoanApplicationJpaRepository jpaRepository;

    @InjectMocks
    private JpaLoanApplicationRepository repository;

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
                "Vivienda"
        );
    }

    @Test
    void shouldSaveLoanApplication() {
        when(jpaRepository.save(application)).thenReturn(application);

        LoanApplication result = repository.save(application);

        assertSame(application, result);
        assertNotNull(result.getLastUpdateDate());
        verify(jpaRepository).save(application);
    }

    @Test
    void shouldGenerateIdWhenSavingNewLoanApplication() {
        application.setId(null);

        when(jpaRepository.save(application)).thenReturn(application);

        LoanApplication result = repository.save(application);

        assertNotNull(result.getId());
        assertNotNull(result.getApplicationDate());
    }

    @Test
    void shouldReturnLoanApplicationById() {
        when(jpaRepository.findById(id))
                .thenReturn(Optional.of(application));

        assertEquals(
                Optional.of(application),
                repository.findById(id)
        );
    }

    @Test
    void shouldReturnAllLoanApplications() {
        when(jpaRepository.findAll()).thenReturn(List.of(application));

        assertEquals(List.of(application), repository.findAll());
    }

    @Test
    void shouldReturnLoanApplicationsByCustomerId() {
        when(jpaRepository.findByCustomerId(customerId))
                .thenReturn(List.of(application));

        assertEquals(
                List.of(application),
                repository.findByCustomerId(customerId)
        );
    }

    @Test
    void shouldReturnLoanApplicationsByStatus() {
        when(jpaRepository.findByStatus(PENDING_EVALUATION.name()))
                .thenReturn(List.of(application));

        assertEquals(
                List.of(application),
                repository.findByStatus(PENDING_EVALUATION.name())
        );
    }

    @Test
    void shouldUpdateLoanApplicationStatus() {
        when(jpaRepository.findById(id))
                .thenReturn(Optional.of(application));

        when(jpaRepository.save(application))
                .thenReturn(application);

        LoanApplication result = repository.updateStatus(
                id,
                CANCELLED.name()
        );

        assertEquals(CANCELLED.name(), result.getStatus());
        verify(jpaRepository).save(application);
    }

    @Test
    void shouldRejectStatusUpdateWhenLoanApplicationDoesNotExist() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.updateStatus(id, CANCELLED.name())
        );

        verify(jpaRepository, never()).save(any());
    }

    @Test
    void shouldDeleteLoanApplication() {
        when(jpaRepository.existsById(id)).thenReturn(true);

        repository.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    void shouldRejectDeleteWhenLoanApplicationDoesNotExist() {
        when(jpaRepository.existsById(id)).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.deleteById(id)
        );

        verify(jpaRepository, never()).deleteById(any());
    }
}