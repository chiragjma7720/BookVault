package com.solvative.bookvault.repository;

import com.solvative.bookvault.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

	boolean existsByIsbn(String isbn);

	java.util.Optional<Book> findByIsbn(String isbn);
}

