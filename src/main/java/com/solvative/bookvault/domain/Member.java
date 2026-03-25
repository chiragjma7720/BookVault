package com.solvative.bookvault.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
public class Member {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "email", nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "name", nullable = false, length = 200)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "membership_status", nullable = false, length = 16)
	private MembershipStatus membershipStatus;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	public Member() {
	}

	public Member(UUID id, String email, String name, MembershipStatus membershipStatus, LocalDateTime joinedAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.membershipStatus = membershipStatus;
		this.joinedAt = joinedAt;
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public MembershipStatus getMembershipStatus() {
		return membershipStatus;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMembershipStatus(MembershipStatus membershipStatus) {
		this.membershipStatus = membershipStatus;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
}

