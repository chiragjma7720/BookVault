package com.solvative.bookvault.repository;

import com.solvative.bookvault.domain.Book;
import com.solvative.bookvault.domain.Loan;
import com.solvative.bookvault.domain.LoanStatus;
import com.solvative.bookvault.domain.Member;
import com.solvative.bookvault.domain.MembershipStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanRepositoryJpaTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private Clock clock;

	@Test
	void overdueQuery_filtersByDueDateAndUnreturned() {
		LocalDateTime now = LocalDateTime.now(clock);

		Book book = bookRepository.save(new Book(
				UUID.randomUUID(),
				"978-1234567891",
				"Test Book",
				"Author",
				"Genre",
				3,
				3
		));
		Member member = memberRepository.save(new Member(
				UUID.randomUUID(),
				"repo-test-member@bookvault.test",
				"Repo Member",
				MembershipStatus.ACTIVE,
				now.minusDays(1)
		));

		Loan overdueActive = new Loan(
				UUID.randomUUID(),
				book,
				member,
				now.minusDays(20),
				now.minusDays(1),
				null,
				LoanStatus.ACTIVE
		);

		Loan notOverdue = new Loan(
				UUID.randomUUID(),
				book,
				member,
				now.minusDays(2),
				now.plusDays(10),
				null,
				LoanStatus.ACTIVE
		);

		Loan alreadyReturned = new Loan(
				UUID.randomUUID(),
				book,
				member,
				now.minusDays(20),
				now.minusDays(10),
				now.minusDays(5),
				LoanStatus.RETURNED
		);

		loanRepository.saveAll(List.of(overdueActive, notOverdue, alreadyReturned));

		Page<Loan> result = loanRepository.findByReturnedAtIsNullAndDueDateBefore(now, PageRequest.of(0, 10));
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(overdueActive.getId());
	}
}

