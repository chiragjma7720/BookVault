package com.solvative.bookvault.repository;

import com.solvative.bookvault.domain.Loan;
import com.solvative.bookvault.domain.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

	long countByMember_IdAndStatus(UUID memberId, LoanStatus status);

	@EntityGraph(attributePaths = {"book", "member"})
	Page<Loan> findByReturnedAtIsNullAndDueDateBefore(LocalDateTime now, Pageable pageable);

	@EntityGraph(attributePaths = {"book", "member"})
	List<Loan> findByStatusAndDueDateBeforeAndReturnedAtIsNull(LoanStatus status, LocalDateTime now);

	@EntityGraph(attributePaths = {"book", "member"})
	List<Loan> findByMember_IdOrderByBorrowedAtDesc(UUID memberId);
}

