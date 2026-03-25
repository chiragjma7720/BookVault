package com.solvative.bookvault.api.dto;

import java.util.UUID;

public record BookResponse(
		UUID id,
		String isbn,
		String title,
		String author,
		String genre,
		int totalCopies,
		int availableCopies
) {
}

