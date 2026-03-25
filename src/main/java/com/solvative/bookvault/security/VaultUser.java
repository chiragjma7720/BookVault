package com.solvative.bookvault.security;

import com.solvative.bookvault.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vault_users")
public class VaultUser {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "email", nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 200)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 16)
	private VaultUserRole role;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	public VaultUser() {
	}

	public VaultUser(UUID id, String email, String passwordHash, VaultUserRole role, Member member) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.member = member;
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public VaultUserRole getRole() {
		return role;
	}

	public Member getMember() {
		return member;
	}
}

