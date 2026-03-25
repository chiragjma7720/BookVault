package com.solvative.bookvault.repository;

import com.solvative.bookvault.security.VaultUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VaultUserRepository extends JpaRepository<VaultUser, UUID> {

	Optional<VaultUser> findByEmail(String email);
}

