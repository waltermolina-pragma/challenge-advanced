package com.loanmanagement.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.application.usecases.LoanApplicationService;
import com.loanmanagement.domain.model.LoanApplication;
import com.loanmanagement.infrastructure.config.exception.ResourceNotFoundException;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationRequest;
import com.loanmanagement.infrastructure.controllers.dto.LoanApplicationResponse;
import com.loanmanagement.infrastructure.controllers.mapper.LoanApplicationMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.loanmanagement.domain.model.LoanStatus.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerTest {
    private static final String BASE_URL = "/loan-applications";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanApplicationService service;

    @MockitoBean
    private LoanApplicationMapper mapper;

    private UUID id;
    private UUID customerId;
    private LoanApplication loan;
    @Mock
    private LoanApplicationResponse response;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        customerId = UUID.randomUUID();
        loan = new LoanApplication(
                id,
                customerId,
                new BigDecimal("50000"),
                36,
                "Vivienda"
        );
        when(response.id()).thenReturn(id);
        when(response.status()).thenReturn(PENDING_EVALUATION.name());
    }

    @Test
    void shouldCreateLoanApplication() throws Exception {
        when(service.createLoanApplication(any(LoanApplicationRequest.class)))
                .thenReturn(loan);
        when(mapper.toResponse(loan)).thenReturn(response);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value(PENDING_EVALUATION.name()));

        verify(service).createLoanApplication(any(LoanApplicationRequest.class));
    }

    @Test
    void shouldRejectInvalidLoanApplicationRequest() throws Exception {
        LoanApplicationRequest request = new LoanApplicationRequest(
                null, BigDecimal.ZERO, 36, "",
                null, null, null, null
        );

        mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnLoanApplicationById() throws Exception {
        when(service.findById(id)).thenReturn(loan);
        when(mapper.toResponse(loan)).thenReturn(response);

        mvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(service).findById(id);
    }

    @Test
    void shouldReturnNotFoundWhenLoanApplicationDoesNotExist() throws Exception {
        when(service.findById(id)).thenThrow(new ResourceNotFoundException("No encontrada"));

        mvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void shouldReturnLoanApplicationsByCustomerId() throws Exception {
        when(service.findByCustomerId(customerId)).thenReturn(List.of(loan));
        when(mapper.toResponseList(List.of(loan))).thenReturn(List.of(response));

        mvc.perform(get(BASE_URL + "/customer/{customerId}", customerId))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void shouldReturnAllLoanApplications() throws Exception {
        when(service.findAll()).thenReturn(List.of(loan));
        when(mapper.toResponseList(List.of(loan))).thenReturn(List.of(response));

        mvc.perform(get(BASE_URL))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void shouldReturnLoanApplicationsByStatus() throws Exception {
        when(service.findByStatus(APPROVED.name())).thenReturn(List.of(loan));
        when(mapper.toResponseList(List.of(loan))).thenReturn(List.of(response));

        mvc.perform(get(BASE_URL).param("status", APPROVED.name()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].id").value(id.toString()));

        verify(service).findByStatus(APPROVED.name());
        verify(service, never()).findAll();
    }

    @Test
    void shouldEvaluateLoanApplication() throws Exception {
        when(service.evaluateAndApprove(id)).thenReturn(loan);
        when(mapper.toResponse(loan)).thenReturn(response);

        mvc.perform(post(BASE_URL + "/{id}/evaluate", id))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(service).evaluateAndApprove(id);
    }

    @Test
    void shouldApproveLoanApplicationWithConditions() throws Exception {
        when(service.approveWithConditions(
                eq(id),
                eq(new BigDecimal("40000")),
                eq("Aval")
        )).thenReturn(loan);

        when(mapper.toResponse(loan)).thenReturn(response);

        mvc.perform(post(BASE_URL + "/{id}/approve-with-conditions", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "approvedAmount": 40000,
                                  "conditions": "Aval"
                                }
                                """))
                .andExpect(status().is(HttpStatus.OK.value()));

        verify(service).approveWithConditions(
                id,
                new BigDecimal("40000"),
                "Aval"
        );
    }

    @Test
    void shouldRejectInvalidApprovalConditions() throws Exception {
        mvc.perform(post(BASE_URL + "/{id}/approve-with-conditions", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "approvedAmount": 0,
                                  "conditions": ""
                                }
                                """))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    void shouldCancelLoanApplication() throws Exception {
        mvc.perform(post(BASE_URL + "/{id}/cancel", id))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        verify(service).cancelApplication(id);
    }

    @Test
    void shouldDeleteLoanApplication() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        verify(service).deleteById(id);
    }

    private LoanApplicationRequest validRequest() {
        return new LoanApplicationRequest(
                customerId,
                new BigDecimal("50000"),
                36,
                "Vivienda",
                LocalDate.now(),
                "Vehiculo",
                BigDecimal.TEN,
                "Cliente no sabe lo que hace :)"
        );
    }
}