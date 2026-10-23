package com.loanmanagement.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanApplicationRequest(
    @NotNull(message = "El ID del cliente es obligatorio")
    UUID customerId,

    @NotNull(message = "El monto solicitado es obligatorio")
    @Positive(message = "El monto solicitado debe ser mayor a cero")
    BigDecimal requestedAmount,

    @NotNull(message = "El plazo del préstamo es obligatorio")
    Integer loanTerm,

    @NotBlank(message = "El propósito del préstamo es obligatorio")
    @Size(max = 255, message = "El propósito no puede exceder 255 caracteres")
    String purpose,

    LocalDate applicationDate,

    @Size(max = 50, message = "El tipo de garantía no puede exceder 50 caracteres")
    String collateralType,

    @Positive(message = "El valor de la garantía debe ser mayor a cero")
    BigDecimal collateralValue,

    @Size(max = 500, message = "Las notas adicionales no pueden exceder 500 caracteres")
    String additionalNotes
) {
    public LoanApplicationRequest(UUID customerId, BigDecimal requestedAmount, Integer loanTerm, String purpose) {
        this(customerId, requestedAmount, loanTerm, purpose, LocalDate.now(), null, null, null);
    }
}