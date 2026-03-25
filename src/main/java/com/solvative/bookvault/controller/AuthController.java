package com.solvative.bookvault.controller;

import com.solvative.bookvault.api.dto.ApiResponse;
import com.solvative.bookvault.api.dto.LoginRequest;
import com.solvative.bookvault.api.dto.LoginResponse;
import com.solvative.bookvault.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
		LoginResponse data = authService.login(request);
		return ResponseEntity.ok(new ApiResponse<>(Instant.now(), data, null));
	}
}

