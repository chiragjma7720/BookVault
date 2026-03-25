package com.solvative.bookvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvative.bookvault.api.dto.BookCreateRequest;
import com.solvative.bookvault.api.dto.LoginRequest;
import com.solvative.bookvault.api.dto.LoanBorrowRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoanFlowIntegrationTest {

	private static final String LIBRARIAN_EMAIL = "librarian@bookvault.test";
	private static final String MEMBER_EMAIL = "member@bookvault.test";
	private static final String PASSWORD = "password123";

	private static final UUID MEMBER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String loginAndGetToken(String email) throws Exception {
		LoginRequest req = new LoginRequest(email, PASSWORD);
		String body = objectMapper.writeValueAsString(req);

		MvcResult result = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andReturn();

		JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
		return root.path("data").path("token").asText();
	}

	private UUID createBook(String bearerToken) throws Exception {
		BookCreateRequest req = new BookCreateRequest(
				"978-1234567892",
				"Integration Book",
				"Integration Author",
				"Fantasy",
				1
		);

		String body = objectMapper.writeValueAsString(req);

		MvcResult result = mockMvc.perform(post("/api/books")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andReturn();

		JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
		return UUID.fromString(root.path("data").path("id").asText());
	}

	private UUID borrowBook(String bearerToken, UUID bookId) throws Exception {
		LoanBorrowRequest req = new LoanBorrowRequest(bookId);
		String body = objectMapper.writeValueAsString(req);

		MvcResult result = mockMvc.perform(post("/api/loans")
						.header("Authorization", "Bearer " + bearerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andReturn();

		JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
		return UUID.fromString(root.path("data").path("id").asText());
	}

	@Test
	@Transactional
	void borrowThenReturn_updatesBookAndLoan() throws Exception {
		String librarianToken = loginAndGetToken(LIBRARIAN_EMAIL);
		UUID bookId = createBook(librarianToken);

		String memberToken = loginAndGetToken(MEMBER_EMAIL);
		UUID loanId = borrowBook(memberToken, bookId);

		// Return it
		mockMvc.perform(put("/api/loans/{id}/return", loanId)
						.header("Authorization", "Bearer " + memberToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("RETURNED"));

		// Availability restored
		mockMvc.perform(get("/api/books/{id}", bookId)
						.header("Authorization", "Bearer " + memberToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.availableCopies").value(1));

		// Member loan history shows returned status
		mockMvc.perform(get("/api/members/{id}/loans", MEMBER_ID)
						.header("Authorization", "Bearer " + memberToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].status").value("RETURNED"));
	}

	@Test
	@Transactional
	void borrowWhenNoAvailableCopies_returnsConflictEnvelope() throws Exception {
		String librarianToken = loginAndGetToken(LIBRARIAN_EMAIL);
		UUID bookId = createBook(librarianToken);

		String memberToken = loginAndGetToken(MEMBER_EMAIL);

		// First borrow consumes the last copy (availableCopies -> 0)
		borrowBook(memberToken, bookId);

		// Second borrow should be rejected
		LoanBorrowRequest req = new LoanBorrowRequest(bookId);
		String body = objectMapper.writeValueAsString(req);

		mockMvc.perform(post("/api/loans")
						.header("Authorization", "Bearer " + memberToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error.code").value("NO_AVAILABLE_COPIES"));
	}
}

