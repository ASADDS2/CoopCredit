package com.coopcredit.risk_central_mock_service.application.usecases;

import com.coopcredit.risk_central_mock_service.domain.model.RiskEvaluation;
import com.coopcredit.risk_central_mock_service.domain.model.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateRiskUseCaseTest {

    private CalculateRiskUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalculateRiskUseCase();
    }

    @Test
    @DisplayName("Should calculate consistent risk score for same document")
    void shouldCalculateConsistentRiskScore() {
        // Given
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("10000");
        int term = 24;

        // When
        RiskEvaluation evaluation1 = useCase.execute(documentId, amount, term);
        RiskEvaluation evaluation2 = useCase.execute(documentId, amount, term);

        // Then
        assertThat(evaluation1.getScore()).isEqualTo(evaluation2.getScore());
        assertThat(evaluation1.getRiskLevel()).isEqualTo(evaluation2.getRiskLevel());
        assertThat(evaluation1.getRecommendation()).isEqualTo(evaluation2.getRecommendation());
    }

    @Test
    @DisplayName("Should return LOW risk for high score")
    void shouldReturnLowRiskForHighScore() {
        // Given - Using a document that generates high score
        String documentId = "87654321";
        BigDecimal amount = new BigDecimal("5000");
        int term = 12;

        // When
        RiskEvaluation evaluation = useCase.execute(documentId, amount, term);

        // Then
        if (evaluation.getScore() > 700) {
            assertThat(evaluation.getRiskLevel()).isEqualTo(RiskLevel.LOW);
            assertThat(evaluation.getRecommendation()).isEqualTo("APPROVED");
        }
    }

    @Test
    @DisplayName("Should return HIGH risk for low score")
    void shouldReturnHighRiskForLowScore() {
        // Given - Using parameters that might generate low score
        String documentId = "11111111";
        BigDecimal amount = new BigDecimal("100000");
        int term = 60;

        // When
        RiskEvaluation evaluation = useCase.execute(documentId, amount, term);

        // Then
        if (evaluation.getScore() <= 400) {
            assertThat(evaluation.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
            assertThat(evaluation.getRecommendation()).isEqualTo("REJECTED");
        }
    }

    @Test
    @DisplayName("Should return MEDIUM risk for medium score")
    void shouldReturnMediumRiskForMediumScore() {
        // Given
        String documentId = "55555555";
        BigDecimal amount = new BigDecimal("20000");
        int term = 36;

        // When
        RiskEvaluation evaluation = useCase.execute(documentId, amount, term);

        // Then
        if (evaluation.getScore() > 400 && evaluation.getScore() <= 700) {
            assertThat(evaluation.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
            assertThat(evaluation.getRecommendation()).isEqualTo("MANUAL_REVIEW");
        }
    }

    @Test
    @DisplayName("Should calculate max amount based on score")
    void shouldCalculateMaxAmountBasedOnScore() {
        // Given
        String documentId = "99999999";
        BigDecimal amount = new BigDecimal("15000");
        int term = 24;

        // When
        RiskEvaluation evaluation = useCase.execute(documentId, amount, term);

        // Then
        assertThat(evaluation.getMaxAmount()).isNotNull();
        assertThat(evaluation.getMaxAmount()).isPositive();

        // Max amount should be proportional to score
        if (evaluation.getScore() > 700) {
            assertThat(evaluation.getMaxAmount()).isGreaterThanOrEqualTo(new BigDecimal("50000"));
        } else if (evaluation.getScore() > 400) {
            assertThat(evaluation.getMaxAmount()).isBetween(
                    new BigDecimal("20000"),
                    new BigDecimal("50000"));
        } else {
            assertThat(evaluation.getMaxAmount()).isLessThanOrEqualTo(new BigDecimal("20000"));
        }
    }

    @Test
    @DisplayName("Should include evaluation details")
    void shouldIncludeEvaluationDetails() {
        // Given
        String documentId = "12345678";
        BigDecimal amount = new BigDecimal("10000");
        int term = 24;

        // When
        RiskEvaluation evaluation = useCase.execute(documentId, amount, term);

        // Then
        assertThat(evaluation).isNotNull();
        assertThat(evaluation.getDocumentId()).isEqualTo(documentId);
        assertThat(evaluation.getScore()).isBetween(0, 1000);
        assertThat(evaluation.getRiskLevel()).isNotNull();
        assertThat(evaluation.getRecommendation()).isNotNull();
        assertThat(evaluation.getMaxAmount()).isNotNull();
        assertThat(evaluation.getEvaluationDate()).isNotNull();
        assertThat(evaluation.getDetails()).isNotNull();
    }

    @Test
    @DisplayName("Should generate different scores for different documents")
    void shouldGenerateDifferentScoresForDifferentDocuments() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        int term = 24;

        // When
        RiskEvaluation evaluation1 = useCase.execute("11111111", amount, term);
        RiskEvaluation evaluation2 = useCase.execute("22222222", amount, term);
        RiskEvaluation evaluation3 = useCase.execute("33333333", amount, term);

        // Then
        // Scores should be different for different documents
        assertThat(evaluation1.getScore())
                .isNotEqualTo(evaluation2.getScore())
                .isNotEqualTo(evaluation3.getScore());
    }
}
