package com.solvative.bookvault.service;

import com.solvative.bookvault.api.dto.LoanBorrowRequest;
import com.solvative.bookvault.api.dto.LoanResponse;
import com.solvative.bookvault.domain.Book;
import com.solvative.bookvault.domain.Loan;
import com.solvative.bookvault.domain.LoanStatus;
import com.solvative.bookvault.domain.Member;
import com.solvative.bookvault.exception.BadRequestException;
import com.solvative.bookvault.exception.ConflictException;
import com.solvative.bookvault.exception.ForbiddenException;
import com.solvative.bookvault.exception.ResourceNotFoundException;
import com.solvative.bookvault.async.LoanOverdueEvent;
import com.solvative.bookvault.repository.BookRepository;
import com.solvative.bookvault.repository.LoanRepository;
import com.solvative.bookvault.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.solvative.bookvault.domain.LoanStatus.ACTIVE;

@Service
public class LoanService {

	private final LoanRepository loanRepository;
	private final BookRepository bookRepository;
	private final MemberRepository memberRepository;
	private final Clock clock;
	private final ApplicationEventPublisher publisher;

	public LoanService(LoanRepository loanRepository,
						BookRepository bookRepository,
						MemberRepository memberRepository,
						Clock clock,
						ApplicationEventPublisher publisher) {
		this.loanRepository = loanRepository;
		this.bookRepository = bookRepository;
		this.memberRepository = memberRepository;
		this.clock = clock;
		this.publisher = publisher;
	}

	@Transactional
	public LoanResponse borrow(UUID bookId, UUID memberId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("BOOK_NOT_FOUND", "Book not found"));
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new ResourceNotFoundException("MEMBER_NOT_FOUND", "Member not found"));

		if (member.getMembershipStatus() != com.solvative.bookvault.domain.MembershipStatus.ACTIVE) {
			throw new ForbiddenException("MEMBER_SUSPENDED", "Suspended members cannot borrow books");
		}

		long activeLoans = loanRepository.countByMember_IdAndStatus(memberId, LoanStatus.ACTIVE);
		if (activeLoans >= 3) {
			throw new ConflictException("ACTIVE_LOAN_LIMIT_REACHED", "A member may not have more than 3 active loans");
		}

		if (book.getAvailableCopies() <= 0) {
			throw new ConflictException("NO_AVAILABLE_COPIES", "No available copies for this book");
		}

		LocalDateTime now = LocalDateTime.now(clock);
		Loan loan = new Loan(
				UUID.randomUUID(),
				book,
				member,
				now,
				now.plusDays(14),
				null,
				LoanStatus.ACTIVE
		);

		book.borrowOne();
		bookRepository.save(book);
		return toResponse(loanRepository.save(loan));
	}

	@Transactional
	public LoanResponse returnLoan(UUID loanId, UUID requesterMemberId, boolean librarian) {
		Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException("LOAN_NOT_FOUND", "Loan not found"));

		if (!librarian && !loan.getMember().getId().equals(requesterMemberId)) {
			throw new ForbiddenException("FORBIDDEN", "You can only return your own loans");
		}

		if (loan.getStatus() == LoanStatus.RETURNED) {
			throw new ConflictException("ALREADY_RETURNED", "This loan has already been returned");
		}

		Book book = loan.getBook();

		if (book.getAvailableCopies() >= book.getTotalCopies()) {
			throw new ConflictException("INVALID_BOOK_STATE", "Book availability is inconsistent");
		}

		LocalDateTime now = LocalDateTime.now(clock);
		loan.setStatus(LoanStatus.RETURNED);
		loan.setReturnedAt(now);
		book.returnOne();

		bookRepository.save(book);
		return toResponse(loanRepository.save(loan));
	}

	public List<LoanResponse> getMemberLoans(UUID memberId) {
		return loanRepository.findByMember_IdOrderByBorrowedAtDesc(memberId).stream()
				.map(this::toResponse)
				.toList();
	}

	public Page<LoanResponse> listOverdue(Pageable pageable) {
		LocalDateTime now = LocalDateTime.now(clock);
		return loanRepository.findByReturnedAtIsNullAndDueDateBefore(now, pageable).map(this::toResponse);
	}

	@Transactional
	public void scanAndMarkOverdue() {
		LocalDateTime now = LocalDateTime.now(clock);
		List<Loan> toMark = loanRepository.findByStatusAndDueDateBeforeAndReturnedAtIsNull(LoanStatus.ACTIVE, now);
		for (Loan loan : toMark) {
			loan.setStatus(LoanStatus.OVERDUE);
			loanRepository.save(loan);
			// Fire-and-forget notification; listener will run asynchronously.
			publisher.publishEvent(new LoanOverdueEvent(loan.getId(), loan.getMember().getId()));
		}
	}

	private LoanResponse toResponse(Loan loan) {
		return new LoanResponse(
				loan.getId(),
				loan.getBook().getId(),
				loan.getMember().getId(),
				loan.getBorrowedAt(),
				loan.getDueDate(),
				loan.getReturnedAt(),
				loan.getStatus()
		);
	}
}

