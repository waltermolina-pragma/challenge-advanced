package com.loanmanagement.domain.ports;

import com.loanmanagement.domain.model.LoanApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository {
    LoanApplication save(LoanApplication loanApplication);

    Optional<LoanApplication> findById(UUID id);

    List<LoanApplication> findAll();

    List<LoanApplication> findByCustomerId(UUID customerId);

    List<LoanApplication> findByStatus(String status);

    LoanApplication updateStatus(UUID id, String status);

    void deleteById(UUID id);
}