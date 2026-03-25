package com.solvative.bookvault.security;

import java.util.UUID;

public record JwtUser(UUID memberId, String email, VaultUserRole role) {
}

