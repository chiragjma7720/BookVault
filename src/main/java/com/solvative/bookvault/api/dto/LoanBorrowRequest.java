package com.solvative.bookvault.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LoanBorrowRequest(@NotNull UUID bookId) {
}

