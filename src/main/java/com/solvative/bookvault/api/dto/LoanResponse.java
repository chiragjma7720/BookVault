package com.solvative.bookvault.api.dto;

import com.solvative.bookvault.domain.LoanStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanResponse(
		UUID id,
		UUID bookId,
		UUID memberId,
		LocalDateTime borrowedAt,
		LocalDateTime dueDate,
		LocalDateTime returnedAt,
		LoanStatus status
) {
}

