package com.loanmanagement.infrastructure.controllers;

import com.loanmanagement.infrastructure.config.exception.ApiErrorResponse;
import com.loanmanagement.infrastructure.config.exception.GlobalExceptionHandler;
import com.loanmanagement.infrastructure.config.exception.ResourceNotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    private static final String PATH = "/loan-applications";

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn(PATH);
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimitIsExceeded() {
        RequestNotPermitted exception = mock(RequestNotPermitted.class);

        ResponseEntity<ApiErrorResponse> response =
                handler.handleRateLimit(exception, request);

        assertError(
                response,
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                "Demasiadas solicitudes. Inténtalo nuevamente más tarde."
        );
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Solicitud no encontrada");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleNotFound(exception, request);

        assertError(
                response,
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "Solicitud no encontrada"
        );
    }

    @Test
    void shouldReturnBadRequestWhenConstraintIsViolated() {
        ConstraintViolationException exception = new ConstraintViolationException(
                "Restricción de validación incumplida",
                null
        );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleConstraintViolation(exception, request);

        assertError(
                response,
                HttpStatus.BAD_REQUEST,
                "CONSTRAINT_VIOLATION",
                "Restricción de validación incumplida"
        );
    }

    @Test
    void shouldReturnBadRequestWhenArgumentIsInvalid() {
        IllegalArgumentException exception = new IllegalArgumentException("Monto inválido");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleIllegalArgument(exception, request);

        assertError(
                response,
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                "Monto inválido"
        );
    }

    @Test
    void shouldReturnConflictWhenStateIsInvalid() {
        IllegalStateException exception = new IllegalStateException("Estado de solicitud inválido");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleIllegalState(exception, request);

        assertError(
                response,
                HttpStatus.CONFLICT,
                "INVALID_STATE",
                "Estado de solicitud inválido"
        );
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionIsUnexpected() {
        Exception exception = new RuntimeException("Error interno de base de datos");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleUnexpected(exception, request);

        assertError(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Ocurrió un error interno. Inténtalo nuevamente."
        );
    }

    private void assertError(
            ResponseEntity<ApiErrorResponse> response,
            HttpStatus expectedStatus,
            String expectedCode,
            String expectedMessage
    ) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiErrorResponse body = response.getBody();

        assertEquals(expectedStatus.value(), body.status());
        assertEquals(expectedCode, body.code());
        assertEquals(expectedMessage, body.message());
        assertEquals(PATH, body.path());
        assertNotNull(body.timestamp());
    }
}