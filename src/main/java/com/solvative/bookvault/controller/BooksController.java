package com.solvative.bookvault.controller;

import com.solvative.bookvault.api.dto.ApiResponse;
import com.solvative.bookvault.api.dto.BookCreateRequest;
import com.solvative.bookvault.api.dto.BookResponse;
import com.solvative.bookvault.api.dto.BookUpdateRequest;
import com.solvative.bookvault.security.JwtUser;
import com.solvative.bookvault.security.VaultUserRole;
import com.solvative.bookvault.service.BookService;
import com.solvative.bookvault.exception.ForbiddenException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BooksController {

	private final BookService bookService;

	public BooksController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<BookResponse>>> list(
			@RequestParam(required = false) String genre,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) Boolean available) {

		String normalizedGenre = (genre == null || genre.isBlank()) ? null : genre;
		String normalizedAuthor = (author == null || author.isBlank()) ? null : author;
		List<BookResponse> data = bookService.listBooks(normalizedGenre, normalizedAuthor, available);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<BookResponse>> getOne(@PathVariable UUID id) {
		BookResponse data = bookService.getBook(id);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<BookResponse>> create(
			@AuthenticationPrincipal JwtUser principal,
			@RequestBody @Valid BookCreateRequest request) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can create books");
		}
		BookResponse data = bookService.createBook(request);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<BookResponse>> update(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id,
			@RequestBody @Valid BookUpdateRequest request) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can update books");
		}
		BookResponse data = bookService.updateBook(id, request);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can delete books");
		}
		bookService.deleteBook(id);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), null, null));
	}
}

