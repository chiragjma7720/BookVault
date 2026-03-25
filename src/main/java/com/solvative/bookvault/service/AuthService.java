package com.solvative.bookvault.service;

import com.solvative.bookvault.api.dto.LoginRequest;
import com.solvative.bookvault.api.dto.LoginResponse;
import com.solvative.bookvault.exception.UnauthorizedException;
import com.solvative.bookvault.security.JwtService;
import com.solvative.bookvault.security.JwtUser;
import com.solvative.bookvault.security.VaultUser;
import com.solvative.bookvault.security.VaultUserRole;
import com.solvative.bookvault.repository.VaultUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final VaultUserRepository vaultUserRepository;
	private final JwtService jwtService;

	public AuthService(AuthenticationManager authenticationManager, VaultUserRepository vaultUserRepository, JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.vaultUserRepository = vaultUserRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public LoginResponse login(LoginRequest request) {
		var authentication = new UsernamePasswordAuthenticationToken(request.email(), request.password());
		try {
			authenticationManager.authenticate(authentication);
		} catch (Exception e) {
			throw new UnauthorizedException("AUTH_FAILED", "Invalid email or password");
		}

		VaultUser vaultUser = vaultUserRepository.findByEmail(request.email())
				.orElseThrow(() -> new UnauthorizedException("AUTH_FAILED", "Invalid email or password"));

		JwtUser user = new JwtUser(vaultUser.getMember().getId(), vaultUser.getEmail(), vaultUser.getRole());
		return new LoginResponse(jwtService.generateToken(user));
	}
}

