package com.solvative.bookvault.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "bookvault.jwt")
public record JwtProperties(
		String secret,
		Duration expiration
) {
}

