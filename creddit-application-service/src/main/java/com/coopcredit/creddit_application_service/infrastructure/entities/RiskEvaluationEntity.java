package com.coopcredit.creddit_application_service.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false, unique = true)
    private CreditApplicationEntity creditApplication;

    @Column(nullable = false, length = 50)
    private String document;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel; // BAJO, MEDIO, ALTO

    @Column(length = 500)
    private String detail;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        if (evaluatedAt == null) {
            evaluatedAt = LocalDateTime.now();
        }
    }
}
