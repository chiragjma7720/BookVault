package com.solvative.bookvault.service;

import com.solvative.bookvault.api.dto.BookCreateRequest;
import com.solvative.bookvault.api.dto.BookResponse;
import com.solvative.bookvault.api.dto.BookUpdateRequest;
import com.solvative.bookvault.domain.Book;
import com.solvative.bookvault.exception.BadRequestException;
import com.solvative.bookvault.exception.ConflictException;
import com.solvative.bookvault.exception.ResourceNotFoundException;
import com.solvative.bookvault.repository.BookRepository;
import com.solvative.bookvault.repository.BookSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Cacheable(value = "books", key = "#genre + '|' + #author + '|' + #available")
	public List<BookResponse> listBooks(String genre, String author, Boolean available) {
		Specification<Book> spec = BookSpecifications.withFilters(genre, author, available);
		return bookRepository.findAll(spec).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	@CacheEvict(value = "books", allEntries = true)
	public BookResponse createBook(BookCreateRequest request) {
		return createBookInternal(request);
	}

	@Transactional
	@CacheEvict(value = "books", allEntries = true)
	public BookResponse updateBook(UUID id, BookUpdateRequest request) {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("BOOK_NOT_FOUND", "Book not found"));

		if (!book.getIsbn().equals(request.isbn())) {
			if (bookRepository.existsByIsbn(request.isbn())) {
				throw new ConflictException("ISBN_ALREADY_EXISTS", "A book with this ISBN already exists");
			}
		}

		if (request.totalCopies() < 1) {
			throw new BadRequestException("INVALID_TOTAL_COPIES", "totalCopies must be at least 1");
		}

		int available = Math.min(book.getAvailableCopies(), request.totalCopies());
		book.setIsbn(request.isbn());
		book.setTitle(request.title());
		book.setAuthor(request.author());
		book.setGenre(request.genre());
		book.setTotalCopies(request.totalCopies());
		book.setAvailableCopies(available);

		return toResponse(bookRepository.save(book));
	}

	@Transactional
	@CacheEvict(value = "books", allEntries = true)
	public void deleteBook(UUID id) {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("BOOK_NOT_FOUND", "Book not found"));
		bookRepository.delete(book);
	}

	public BookResponse getBook(UUID id) {
		return bookRepository.findById(id)
				.map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("BOOK_NOT_FOUND", "Book not found"));
	}

	private BookResponse createBookInternal(BookCreateRequest request) {
		if (bookRepository.existsByIsbn(request.isbn())) {
			throw new ConflictException("ISBN_ALREADY_EXISTS", "A book with this ISBN already exists");
		}
		Book book = new Book(
				UUID.randomUUID(),
				request.isbn(),
				request.title(),
				request.author(),
				request.genre(),
				request.totalCopies(),
				request.totalCopies()
		);
		return toResponse(bookRepository.save(book));
	}

	private BookResponse toResponse(Book book) {
		return new BookResponse(
				book.getId(),
				book.getIsbn(),
				book.getTitle(),
				book.getAuthor(),
				book.getGenre(),
				book.getTotalCopies(),
				book.getAvailableCopies()
		);
	}
}

