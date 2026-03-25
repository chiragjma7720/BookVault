package com.solvative.bookvault.repository;

import com.solvative.bookvault.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	List<Member> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}

