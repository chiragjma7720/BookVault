package com.solvative.bookvault.controller;

import com.solvative.bookvault.api.dto.ApiResponse;
import com.solvative.bookvault.api.dto.LoanBorrowRequest;
import com.solvative.bookvault.api.dto.LoanResponse;
import com.solvative.bookvault.exception.ForbiddenException;
import com.solvative.bookvault.security.JwtUser;
import com.solvative.bookvault.security.VaultUserRole;
import com.solvative.bookvault.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LoansController {

	private final LoanService loanService;

	public LoansController(LoanService loanService) {
		this.loanService = loanService;
	}

	@PostMapping("/loans")
	public ResponseEntity<ApiResponse<LoanResponse>> borrow(
			@AuthenticationPrincipal JwtUser principal,
			@RequestBody @Valid LoanBorrowRequest request) {
		LoanResponse data = loanService.borrow(request.bookId(), principal.memberId());
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@PutMapping("/loans/{id}/return")
	public ResponseEntity<ApiResponse<LoanResponse>> returnLoan(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id) {
		boolean librarian = principal.role() == VaultUserRole.LIBRARIAN;
		LoanResponse data = loanService.returnLoan(id, principal.memberId(), librarian);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@GetMapping("/loans/overdue")
	public ResponseEntity<ApiResponse<Page<LoanResponse>>> overdue(
			@AuthenticationPrincipal JwtUser principal,
			@PageableDefault(size = 20) Pageable pageable) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can view overdue loans");
		}
		Page<LoanResponse> data = loanService.listOverdue(pageable);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}

	@GetMapping("/members/{id}/loans")
	public ResponseEntity<ApiResponse<java.util.List<LoanResponse>>> loanHistory(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id) {
		if (principal.role() != VaultUserRole.LIBRARIAN && !principal.memberId().equals(id)) {
			throw new ForbiddenException("FORBIDDEN", "You can only view your own loan history");
		}
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), loanService.getMemberLoans(id), null));
	}
}

