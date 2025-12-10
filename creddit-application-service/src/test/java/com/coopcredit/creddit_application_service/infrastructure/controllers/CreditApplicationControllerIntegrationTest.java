package com.coopcredit.creddit_application_service.infrastructure.controllers;

import com.coopcredit.creddit_application_service.domain.model.ApplicationStatus;
import com.coopcredit.creddit_application_service.infrastructure.web.dto.credit.CreditApplicationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CreditApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Token would be generated in a real test
        jwtToken = "Bearer test-jwt-token";
    }

    @Test
    @DisplayName("Should create credit application successfully")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldCreateCreditApplicationSuccessfully() throws Exception {
        CreditApplicationRequest request = new CreditApplicationRequest();
        request.setAffiliateId(1L);
        request.setAmount(new BigDecimal("10000"));
        request.setTerm(24);
        request.setPurpose("Home improvement");

        mockMvc.perform(post("/api/credit-applications")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is(ApplicationStatus.PENDING.toString())));
    }

    @Test
    @DisplayName("Should get credit application by ID")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldGetCreditApplicationById() throws Exception {
        mockMvc.perform(get("/api/credit-applications/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("Should list credit applications for affiliate")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldListCreditApplicationsForAffiliate() throws Exception {
        mockMvc.perform(get("/api/credit-applications")
                .header("Authorization", jwtToken)
                .param("affiliateId", "1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", isA(List.class)));
    }

    @Test
    @DisplayName("Should evaluate credit application")
    @WithMockUser(username = "analyst", roles = { "ANALISTA" })
    void shouldEvaluateCreditApplication() throws Exception {
        mockMvc.perform(post("/api/credit-applications/1/evaluate")
                .header("Authorization", jwtToken)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.riskEvaluation", notNullValue()));
    }

    @Test
    @DisplayName("Should reject unauthorized access")
    void shouldRejectUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/credit-applications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject access for wrong role")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldRejectAccessForWrongRole() throws Exception {
        // Affiliate trying to access analyst endpoint
        mockMvc.perform(get("/api/credit-applications/pending")
                .header("Authorization", jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should validate request body")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldValidateRequestBody() throws Exception {
        CreditApplicationRequest request = new CreditApplicationRequest();
        // Missing required fields

        mockMvc.perform(post("/api/credit-applications")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Should handle business exceptions")
    @WithMockUser(username = "affiliate", roles = { "AFILIADO" })
    void shouldHandleBusinessExceptions() throws Exception {
        CreditApplicationRequest request = new CreditApplicationRequest();
        request.setAffiliateId(999L); // Non-existent affiliate
        request.setAmount(new BigDecimal("10000"));
        request.setTerm(24);
        request.setPurpose("Test");

        mockMvc.perform(post("/api/credit-applications")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Affiliate not found")));
    }
}
