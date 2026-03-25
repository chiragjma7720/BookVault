package com.solvative.bookvault.api.dto;

import com.solvative.bookvault.validation.ValidIsbn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BookUpdateRequest(
		@ValidIsbn
		@NotBlank
		String isbn,
		@NotBlank
		String title,
		@NotBlank
		String author,
		@NotBlank
		String genre,
		@Min(1)
		int totalCopies
) {
}

