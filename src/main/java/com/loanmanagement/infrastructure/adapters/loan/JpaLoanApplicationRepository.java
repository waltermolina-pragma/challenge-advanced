package com.loanmanagement.infrastructure.adapters.loan;

import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.domain.ports.LoanApplicationRepository;
import com.loanmanagement.infrastructure.repositories.LoanApplicationJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLoanApplicationRepository implements LoanApplicationRepository {
    private final LoanApplicationJpaRepository jpaRepository;

    public JpaLoanApplicationRepository(LoanApplicationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LoanApplication save(LoanApplication loanApplication) {
        if (loanApplication.getId() == null) {
            loanApplication.setId(UUID.randomUUID());
            loanApplication.setApplicationDate(LocalDateTime.now());
        }
        loanApplication.setLastUpdateDate(LocalDateTime.now());
        return jpaRepository.save(loanApplication);
    }

    @Override
    public Optional<LoanApplication> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<LoanApplication> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<LoanApplication> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId);
    }

    @Override
    public List<LoanApplication> findByStatus(String status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public LoanApplication updateStatus(UUID id, String status) {
        Optional<LoanApplication> existing = jpaRepository.findById(id);
        if (existing.isPresent()) {
            LoanApplication loanApplication = existing.get();
            loanApplication.setStatus(status);
            loanApplication.setLastUpdateDate(LocalDateTime.now());
            return jpaRepository.save(loanApplication);
        }
        throw new IllegalArgumentException("Loan application not found with id: " + id);
    }

    @Override
    public void deleteById(UUID id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Cannot delete non-existent loan application: " + id);
        }
    }
}