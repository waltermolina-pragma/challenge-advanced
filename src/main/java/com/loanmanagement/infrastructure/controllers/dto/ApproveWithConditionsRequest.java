package com.loanmanagement.infrastructure.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApproveWithConditionsRequest(
        @NotNull
        @Positive
        BigDecimal approvedAmount,
        @NotBlank
        String conditions
) {
}
