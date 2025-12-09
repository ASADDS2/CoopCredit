package com.coopcredit.creddit_application_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreditApplication {

    private Long id;
    private Long affiliateId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private BigDecimal proposedRate;
    private LocalDate applicationDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CreditApplication() {
    }

    public CreditApplication(Long id, Long affiliateId, BigDecimal requestedAmount,
            Integer termMonths, BigDecimal proposedRate,
            LocalDate applicationDate, String status, String rejectionReason,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.proposedRate = proposedRate;
        this.applicationDate = applicationDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(BigDecimal proposedRate) {
        this.proposedRate = proposedRate;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
