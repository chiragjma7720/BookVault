package com.solvative.bookvault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvative.bookvault.api.dto.BookResponse;
import com.solvative.bookvault.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servletAu.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BooksController.class)
@AutoConfigureMockMvc(addFilters = false)
class BooksControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void listBooks_returnsEnvelopeAndData() throws Exception {
		UUID id = UUID.randomUUID();
		List<BookResponse> books = List.of(new BookResponse(
				id,
				"978-1234567890",
				"The Hobbit",
				"J.R.R. Tolkien",
				"Fantasy",
				5,
				2
		));

		when(bookService.listBooks(eq("Fantasy"), eq("Tolkien"), eq(true))).thenReturn(books);

		mockMvc.perform(get("/api/books")
						.param("genre", "Fantasy")
						.param("author", "Tolkien")
						.param("available", "true")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.data[0].id").value(id.toString()))
				.andExpect(jsonPath("$.data[0].title").value("The Hobbit"));
	}
}

