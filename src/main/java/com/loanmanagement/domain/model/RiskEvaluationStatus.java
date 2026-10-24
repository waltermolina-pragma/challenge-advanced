package com.loanmanagement.domain.model;

import java.util.Locale;

public enum RiskEvaluationStatus {
    APPROVED,
    REJECTED,
    REQUIRES_REVIEW;

    public static RiskEvaluationStatus fromExternal(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El estado de evaluación es obligatorio");
        }
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "APPROVED" -> APPROVED;
            case "REJECTED" -> REJECTED;
            case "REQUIRES_REVIEW" -> REQUIRES_REVIEW;
            default -> throw new IllegalArgumentException("Estado de evaluación desconocido: " + value);
        };
    }
}
