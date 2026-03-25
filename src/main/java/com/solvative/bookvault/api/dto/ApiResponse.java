package com.solvative.bookvault.api.dto;

import java.time.Instant;

public record ApiResponse<T>(Instant timestamp, T data, ApiError error) {
}

