package com.solvative.bookvault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	private final JwtProperties properties;

	public JwtService(JwtProperties properties) {
		this.properties = properties;
	}

	public String generateToken(JwtUser user) {
		Instant now = Instant.now();
		Date exp = Date.from(now.plus(properties.expiration()));

		return Jwts.builder()
				.setSubject(user.memberId().toString())
				.claim("email", user.email())
				.claim("role", user.role().name())
				.setIssuedAt(Date.from(now))
				.setExpiration(exp)
				.signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
				.compact();
	}

	public JwtUser parse(String token) {
		Claims claims = parseClaims(token);
		UUID memberId = UUID.fromString(claims.getSubject());
		String email = claims.get("email", String.class);
		String role = claims.get("role", String.class);
		return new JwtUser(memberId, email, VaultUserRole.valueOf(role));
	}

	public boolean isValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.setSigningKey(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}

