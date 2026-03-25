package com.solvative.bookvault.api.dto;

import com.solvative.bookvault.domain.MembershipStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberCreateRequest(
		@Email
		@NotBlank
		String email,
		@NotBlank
		String name,
		@NotNull
		MembershipStatus membershipStatus
) {
}

