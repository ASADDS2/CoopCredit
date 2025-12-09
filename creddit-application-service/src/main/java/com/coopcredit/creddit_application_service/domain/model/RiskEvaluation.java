package com.coopcredit.creddit_application_service.domain.model;

import java.time.LocalDateTime;

public class RiskEvaluation {

    private Long id;
    private Long creditApplicationId;
    private String document;
    private Integer score;
    private String riskLevel; // BAJO, MEDIO, ALTO
    private String detail;
    private LocalDateTime evaluatedAt;

    public RiskEvaluation() {
    }

    public RiskEvaluation(Long id, Long creditApplicationId, String document,
            Integer score, String riskLevel, String detail,
            LocalDateTime evaluatedAt) {
        this.id = id;
        this.creditApplicationId = creditApplicationId;
        this.document = document;
        this.score = score;
        this.riskLevel = riskLevel;
        this.detail = detail;
        this.evaluatedAt = evaluatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreditApplicationId() {
        return creditApplicationId;
    }

    public void setCreditApplicationId(Long creditApplicationId) {
        this.creditApplicationId = creditApplicationId;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

}
