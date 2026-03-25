package com.solvative.bookvault.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static JwtUser currentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			throw new IllegalStateException("No authentication");
		}
		if (!(auth.getPrincipal() instanceof JwtUser jwtUser)) {
			throw new IllegalStateException("Unexpected principal type");
		}
		return jwtUser;
	}

	public static boolean isLibrarian() {
		return currentUser().role() == VaultUserRole.LIBRARIAN;
	}
}

