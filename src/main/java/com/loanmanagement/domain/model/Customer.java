package com.loanmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private final String name;
    private final String identificationNumber;
    private final LocalDate birthDate;
    private final BigDecimal monthlyIncome;
    private final String employmentStatus;
    private final String creditScore;

    public Customer(UUID id, String name, String identificationNumber, LocalDate birthDate,
                    BigDecimal monthlyIncome, String employmentStatus, String creditScore) {
        this.id = Objects.requireNonNull(id, "Customer ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.identificationNumber = Objects.requireNonNull(identificationNumber, "Identification number cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.monthlyIncome = Objects.requireNonNull(monthlyIncome, "Monthly income cannot be null");
        this.employmentStatus = Objects.requireNonNull(employmentStatus, "Employment status cannot be null");
        this.creditScore = Objects.requireNonNull(creditScore, "Credit score cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (identificationNumber.isBlank()) {
            throw new IllegalArgumentException("Identification number cannot be blank");
        }
        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly income must be positive");
        }
        if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Customer must be at least 18 years old");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public boolean isEligibleForLoan(BigDecimal requestedAmount) {
        if (requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal incomeThreshold = new BigDecimal("1000.00");
        if (monthlyIncome.compareTo(incomeThreshold) < 0) {
            return false;
        }

        if (creditScore.compareTo("600") < 0) {
            return false;
        }

        BigDecimal maxAllowedAmount = monthlyIncome.multiply(new BigDecimal("10"));
        return requestedAmount.compareTo(maxAllowedAmount) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}