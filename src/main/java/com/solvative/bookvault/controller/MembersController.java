package com.solvative.bookvault.controller;

import com.solvative.bookvault.api.dto.ApiResponse;
import com.solvative.bookvault.api.dto.MemberCreateRequest;
import com.solvative.bookvault.api.dto.MemberResponse;
import com.solvative.bookvault.api.dto.MemberUpdateRequest;
import com.solvative.bookvault.exception.ForbiddenException;
import com.solvative.bookvault.security.JwtUser;
import com.solvative.bookvault.security.VaultUserRole;
import com.solvative.bookvault.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MembersController {

	private final MemberService memberService;

	public MembersController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<MemberResponse>> create(
			@AuthenticationPrincipal JwtUser principal,
			@RequestBody @Valid MemberCreateRequest request) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can create members");
		}
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), memberService.createMember(request), null));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MemberResponse>> getOne(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can view members");
		}
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), memberService.getMember(id), null));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MemberResponse>> update(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id,
			@RequestBody @Valid MemberUpdateRequest request) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can update members");
		}
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), memberService.updateMember(id, request), null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(
			@AuthenticationPrincipal JwtUser principal,
			@PathVariable UUID id) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can delete members");
		}
		memberService.deleteMember(id);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), null, null));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<MemberResponse>>> search(
			@AuthenticationPrincipal JwtUser principal,
			@RequestParam("q") String q) {
		if (principal.role() != VaultUserRole.LIBRARIAN) {
			throw new ForbiddenException("FORBIDDEN", "Only librarians can search members");
		}
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), memberService.searchMembers(q), null));
	}
}

