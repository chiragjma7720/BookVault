package com.solvative.bookvault.api.dto;

import com.solvative.bookvault.domain.MembershipStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberResponse(
		UUID id,
		String email,
		String name,
		MembershipStatus membershipStatus,
		LocalDateTime joinedAt
) {
}

