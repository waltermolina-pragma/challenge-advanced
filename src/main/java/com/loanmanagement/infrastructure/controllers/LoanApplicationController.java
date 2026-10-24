package com.loanmanagement.infrastructure.controllers;

import com.loanmanagement.application.usecases.LoanApplicationService;
import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.infrastructure.controllers.dto.ApproveWithConditionsRequest;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationRequest;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationResponse;
import com.loanmanagement.infrastructure.controllers.mapper.LoanApplicationMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-applications")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;

    private final LoanApplicationMapper loanApplicationMapper;

    public LoanApplicationController(LoanApplicationService loanApplicationService, LoanApplicationMapper loanApplicationMapper) {
        this.loanApplicationService = loanApplicationService;
        this.loanApplicationMapper = loanApplicationMapper;
    }

    @PostMapping
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<LoanApplicationResponse> createLoanApplication(
            @Valid @RequestBody LoanApplicationRequest request) {
        LoanApplication response = loanApplicationService.createLoanApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanApplicationMapper.toResponse(response));
    }

    @GetMapping("/{id}")
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<LoanApplicationResponse> getLoanApplicationById(@PathVariable UUID id) {
        return loanApplicationService.findById(id)
                .map(loanApplicationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<List<LoanApplicationResponse>> getLoanApplicationsByCustomerId(
            @PathVariable UUID customerId) {
        List<LoanApplication> responses = loanApplicationService.findByCustomerId(customerId);
        return ResponseEntity.ok(loanApplicationMapper.toResponseList(responses));
    }

    @GetMapping
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoanApplications(
            @RequestParam(required = false) String status) {
        List<LoanApplication> responses = (status != null)
                ? loanApplicationService.findByStatus(status)
                : loanApplicationService.findAll();
        return ResponseEntity.ok(loanApplicationMapper.toResponseList(responses));
    }

    @PostMapping("/{id}/evaluate")
    public ResponseEntity<LoanApplicationResponse> evaluate(@PathVariable UUID id) {
        LoanApplication evaluated = loanApplicationService.evaluateAndApprove(id);
        return ResponseEntity.ok(loanApplicationMapper.toResponse(evaluated));
    }

    @PostMapping("/{id}/approve-with-conditions")
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<LoanApplicationResponse> approveWithConditions(
            @PathVariable UUID id,
            @Valid @RequestBody ApproveWithConditionsRequest request) {
        LoanApplication approved =
                loanApplicationService.approveWithConditions(
                        id,
                        request.approvedAmount(),
                        request.conditions()
                );
        return ResponseEntity.ok(loanApplicationMapper.toResponse(approved));
    }

    @PostMapping("/{id}/cancel")
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        boolean cancelled = loanApplicationService.cancelApplication(id);
        return cancelled ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @RateLimiter(name = "loanApplication")
    public ResponseEntity<Void> deleteLoanApplication(@PathVariable UUID id) {
        loanApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}