package com.loanmanagement.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal requestedAmount;
    private Integer termInMonths;
    private String purpose;
    private String status;
    private LocalDateTime applicationDate;
    private LocalDateTime lastUpdateDate;
    private String evaluatedBy;
    private String rejectionReason;
    private BigDecimal approvedAmount;
    private Integer approvedTerm;
    private BigDecimal interestRate;

    @Transient
    private RiskEvaluation riskEvaluation;

    @Transient
    private Customer customer;

    @Transient
    private String loanTerm;

    @Transient
    private LocalDateTime approvalDate;

    @Transient
    private String collateralType;

    @Transient
    private BigDecimal collateralValue;

    @Transient
    private String additionalNotes;

    @Transient
    private String approvalConditions;

    public LoanApplication() {
    }

    public LoanApplication(UUID id, UUID customerId, BigDecimal requestedAmount, 
                          Integer termInMonths, String purpose) {
        this.id = id;
        this.customerId = customerId;
        this.requestedAmount = requestedAmount;
        this.termInMonths = termInMonths;
        this.purpose = purpose;
        this.status = "PENDING";
        this.applicationDate = LocalDateTime.now();
        this.lastUpdateDate = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermInMonths() {
        return termInMonths;
    }

    public void setTermInMonths(Integer termInMonths) {
        this.termInMonths = termInMonths;
    }

    public Integer getTermMonths() {
        return termInMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termInMonths = termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdateDate = LocalDateTime.now();
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(String evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Integer getApprovedTerm() {
        return approvedTerm;
    }

    public void setApprovedTerm(Integer approvedTerm) {
        this.approvedTerm = approvedTerm;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public RiskEvaluation getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskEvaluation riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getLoanTerm() {
        return loanTerm != null ? loanTerm : (termInMonths != null ? termInMonths + " months" : null);
    }

    public void setLoanTerm(String loanTerm) {
        this.loanTerm = loanTerm;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public BigDecimal getCollateralValue() {
        return collateralValue;
    }

    public void setCollateralValue(BigDecimal collateralValue) {
        this.collateralValue = collateralValue;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getApprovalConditions() {
        return approvalConditions;
    }

    public void setApprovalConditions(String approvalConditions) {
        this.approvalConditions = approvalConditions;
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }

    public boolean hasRiskEvaluation() {
        return this.riskEvaluation != null;
    }

    public boolean isApprovedByRisk() {
        return hasRiskEvaluation() && this.riskEvaluation.isApproved();
    }

    public boolean exceedsMaxAmount(BigDecimal maxAmount) {
        return this.requestedAmount.compareTo(maxAmount) > 0;
    }

    public boolean exceedsMaxTerm(Integer maxTerm) {
        return this.termInMonths != null && this.termInMonths > maxTerm;
    }

    public void approve(BigDecimal approvedAmount, Integer approvedTerm, BigDecimal interestRate) {
        this.status = "APPROVED";
        this.approvedAmount = approvedAmount;
        this.approvedTerm = approvedTerm;
        this.interestRate = interestRate;
        this.lastUpdateDate = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = "REJECTED";
        this.rejectionReason = reason;
        this.lastUpdateDate = LocalDateTime.now();
    }

    public void assignRiskEvaluation(RiskEvaluation evaluation) {
        this.riskEvaluation = evaluation;
        this.lastUpdateDate = LocalDateTime.now();
    }
}