package com.loanmanagement.infrastructure.repositories;

import com.loanmanagement.domain.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApplicationJpaRepository extends JpaRepository<LoanApplication, UUID> {
    List<LoanApplication> findByCustomerId(UUID customerId);

    List<LoanApplication> findByStatus(String status);
}