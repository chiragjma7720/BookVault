package com.solvative.bookvault.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loans")
public class Loan {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "borrowed_at", nullable = false)
	private LocalDateTime borrowedAt;

	@Column(name = "due_date", nullable = false)
	private LocalDateTime dueDate;

	@Column(name = "returned_at")
	private LocalDateTime returnedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 16)
	private LoanStatus status;

	public Loan() {
	}

	public Loan(UUID id, Book book, Member member, LocalDateTime borrowedAt, LocalDateTime dueDate, LocalDateTime returnedAt, LoanStatus status) {
		this.id = id;
		this.book = book;
		this.member = member;
		this.borrowedAt = borrowedAt;
		this.dueDate = dueDate;
		this.returnedAt = returnedAt;
		this.status = status;
	}

	public UUID getId() {
		return id;
	}

	public Book getBook() {
		return book;
	}

	public Member getMember() {
		return member;
	}

	public LocalDateTime getBorrowedAt() {
		return borrowedAt;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public LocalDateTime getReturnedAt() {
		return returnedAt;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public void setReturnedAt(LocalDateTime returnedAt) {
		this.returnedAt = returnedAt;
	}
}

