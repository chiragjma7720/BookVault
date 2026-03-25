package com.solvative.bookvault.async;

import java.util.UUID;

public record LoanOverdueEvent(UUID loanId, UUID memberId) {
}

