package com.solvative.bookvault.exception;

import com.solvative.bookvault.api.dto.ApiError;
import com.solvative.bookvault.api.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex, HttpServletRequest request) {
		ApiError apiError = ApiError.of(ex.getCode(), ex.getMessage(), ex.getDetails());
		return ResponseEntity.status(ex.getStatus()).body(new ApiResponse<>(Instant.now(), null, apiError));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(this::formatFieldError)
				.toList();
		ApiError apiError = ApiError.of("VALIDATION_ERROR", "Request validation failed", details);
		return ResponseEntity.badRequest().body(new ApiResponse<>(Instant.now(), null, apiError));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
		ApiError apiError = ApiError.of("FORBIDDEN", ex.getMessage());
		return ResponseEntity.status(403).body(new ApiResponse<>(Instant.now(), null, apiError));
	}

	// Fallback: don't leak internal error messages
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex) {
		ApiError apiError = ApiError.of("INTERNAL_ERROR", "Unexpected error");
		return ResponseEntity.status(500).body(new ApiResponse<>(Instant.now(), null, apiError));
	}

	private String formatFieldError(FieldError err) {
		String field = err.getField();
		String msg = err.getDefaultMessage() == null ? "invalid" : err.getDefaultMessage();
		return field + ": " + msg;
	}
}

