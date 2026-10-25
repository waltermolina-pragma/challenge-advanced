package com.loanmanagement.infrastructure.adapters.risk.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ExternalRiskResponse(
        String status,
        Integer riskScore,
        List<String> warnings
) {
}
