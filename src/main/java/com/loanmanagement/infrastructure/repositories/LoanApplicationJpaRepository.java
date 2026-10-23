package com.loanmanagement.infrastructure.repositories;

import com.loanmanagement.domain.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApplicationJpaRepository extends JpaRepository<LoanApplication, UUID> {

    List<LoanApplication> findByCustomerId(UUID customerId);

    List<LoanApplication> findByStatus(String status);

    @Query("SELECT la FROM LoanApplication la WHERE la.customerId = :customerId AND la.status = :status")
    List<LoanApplication> findByCustomerIdAndStatus(
        @Param("customerId") UUID customerId,
        @Param("status") String status
    );

    @Query("SELECT la FROM LoanApplication la WHERE la.applicationDate BETWEEN :startDate AND :endDate")
    List<LoanApplication> findByApplicationDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT la FROM LoanApplication la WHERE la.requestedAmount >= :minAmount")
    List<LoanApplication> findByRequestedAmountGreaterThanEqual(
        @Param("minAmount") java.math.BigDecimal minAmount
    );

    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT la.status, COUNT(la) FROM LoanApplication la GROUP BY la.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT la FROM LoanApplication la WHERE la.customerId = :customerId ORDER BY la.applicationDate DESC")
    List<LoanApplication> findRecentByCustomerId(
        @Param("customerId") UUID customerId
    );

    boolean existsByCustomerIdAndStatus(UUID customerId, String status);
}