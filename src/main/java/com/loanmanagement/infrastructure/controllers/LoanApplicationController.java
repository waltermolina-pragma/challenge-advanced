package com.loanmanagement.infrastructure.controllers;

import com.loanmanagement.application.usecases.LoanApplicationService;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationRequest;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping
    public ResponseEntity<LoanApplicationResponse> createLoanApplication(
            @Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = loanApplicationService.createLoanApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponse> getLoanApplicationById(@PathVariable UUID id) {
        return loanApplicationService.findById(id)
                .map(app -> LoanApplicationResponse.fromDomain(app, app.getCustomer(), app.getRiskEvaluation()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplicationResponse>> getLoanApplicationsByCustomerId(
            @PathVariable UUID customerId) {
        List<LoanApplicationResponse> responses = loanApplicationService.findByCustomerId(customerId)
                .stream()
                .map(app -> LoanApplicationResponse.fromDomain(app, app.getCustomer(), app.getRiskEvaluation()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoanApplications(
            @RequestParam(required = false) String status) {
        List<LoanApplicationResponse> responses = (status != null) 
                ? loanApplicationService.findByStatus(status).stream().map(app -> LoanApplicationResponse.fromDomain(app, app.getCustomer(), app.getRiskEvaluation())).collect(Collectors.toList())
                : loanApplicationService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanApplicationResponse> updateLoanApplicationStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return loanApplicationService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoanApplication(@PathVariable UUID id) {
        loanApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}