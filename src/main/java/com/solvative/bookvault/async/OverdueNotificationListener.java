package com.solvative.bookvault.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OverdueNotificationListener {

	private static final Logger log = LoggerFactory.getLogger(OverdueNotificationListener.class);

	@Async
	@EventListener
	public void onLoanOverdue(LoanOverdueEvent event) {
		// Exercise requirement: log a "notification sent" message.
		log.info("notification sent for overdue loan={} member={}", event.loanId(), event.memberId());
	}
}

