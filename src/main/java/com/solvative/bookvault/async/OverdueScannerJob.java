package com.solvative.bookvault.async;

import com.solvative.bookvault.service.LoanService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueScannerJob {

	private final LoanService loanService;

	public OverdueScannerJob(LoanService loanService) {
		this.loanService = loanService;
	}

	@Scheduled(cron = "${bookvault.overdue.scan-cron:0 0 2 * * *}")
	public void scan() {
		loanService.scanAndMarkOverdue();
	}
}

