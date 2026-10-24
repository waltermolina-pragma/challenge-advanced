package com.loanmanagement.domain.model;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH;

    public static RiskLevel fromScore(Integer score) {
        if (score == null) {
            throw new IllegalArgumentException("La puntuación de riesgo es obligatoria");
        }
        if (score >= 700) {
            return LOW;
        }
        if (score >= 400) {
            return MEDIUM;
        }
        return HIGH;
    }
}