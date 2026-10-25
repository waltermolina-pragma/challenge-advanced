package com.loanmanagement.model;

import com.loanmanagement.domain.model.Customer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer(BigDecimal income, String score) {
        return new Customer(
                UUID.randomUUID(),
                "Juan Pérez",
                "12345678",
                LocalDate.now().minusYears(30),
                income,
                "EMPLOYED",
                score
        );
    }

    @Test
    void shouldCreateCustomer() {
        Customer customer = customer(new BigDecimal("5000"), "750");

        assertNotNull(customer.getId());
        assertEquals("Juan Pérez", customer.getName());
        assertEquals("12345678", customer.getIdentificationNumber());
        assertEquals("EMPLOYED", customer.getEmploymentStatus());
    }

    @Test
    void shouldRejectNullRequiredFields() {
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.now().minusYears(30);
        BigDecimal income = new BigDecimal("5000");

        assertThrows(NullPointerException.class,
                () -> new Customer(null, "Juan", "123", birthDate, income, "EMPLOYED", "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, null, "123", birthDate, income, "EMPLOYED", "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, "Juan", null, birthDate, income, "EMPLOYED", "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, "Juan", "123", null, income, "EMPLOYED", "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, "Juan", "123", birthDate, null, "EMPLOYED", "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, "Juan", "123", birthDate, income, null, "750"));

        assertThrows(NullPointerException.class,
                () -> new Customer(id, "Juan", "123", birthDate, income, "EMPLOYED", null));
    }

    @Test
    void shouldRejectBlankNameAndIdentificationNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(
                        UUID.randomUUID(), " ", "123",
                        LocalDate.now().minusYears(30),
                        new BigDecimal("5000"), "EMPLOYED", "750"
                ));

        assertThrows(IllegalArgumentException.class,
                () -> new Customer(
                        UUID.randomUUID(), "Juan", " ",
                        LocalDate.now().minusYears(30),
                        new BigDecimal("5000"), "EMPLOYED", "750"
                ));
    }

    @Test
    void shouldRejectNonPositiveMonthlyIncome() {
        assertThrows(IllegalArgumentException.class,
                () -> customer(BigDecimal.ZERO, "750"));

        assertThrows(IllegalArgumentException.class,
                () -> customer(new BigDecimal("-100"), "750"));
    }

    @Test
    void shouldRejectUnderageCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(
                        UUID.randomUUID(), "Juan", "123",
                        LocalDate.now().minusYears(17),
                        new BigDecimal("5000"), "EMPLOYED", "750"
                ));
    }

    @Test
    void shouldAllowEligibleCustomer() {
        Customer customer = customer(new BigDecimal("5000"), "750");

        assertTrue(customer.isEligibleForLoan(new BigDecimal("40000")));
        assertTrue(customer.isEligibleForLoan(new BigDecimal("50000")));
    }

    @Test
    void shouldRejectLoanWhenAmountIsInvalid() {
        Customer customer = customer(new BigDecimal("5000"), "750");

        assertFalse(customer.isEligibleForLoan(BigDecimal.ZERO));
        assertFalse(customer.isEligibleForLoan(new BigDecimal("-100")));
    }

    @Test
    void shouldRejectLoanWhenIncomeIsInsufficient() {
        Customer customer = customer(new BigDecimal("900"), "750");

        assertFalse(customer.isEligibleForLoan(new BigDecimal("5000")));
    }

    @Test
    void shouldRejectLoanWhenCreditScoreIsLow() {
        Customer customer = customer(new BigDecimal("5000"), "500");

        assertFalse(customer.isEligibleForLoan(new BigDecimal("10000")));
    }

    @Test
    void shouldRejectLoanWhenAmountExceedsIncomeLimit() {
        Customer customer = customer(new BigDecimal("5000"), "750");

        assertFalse(customer.isEligibleForLoan(new BigDecimal("50001")));
    }
}