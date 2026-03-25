package com.solvative.bookvault.repository;

import com.solvative.bookvault.domain.Book;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class BookSpecifications {

	private BookSpecifications() {
	}

	public static Specification<Book> withFilters(String genre, String author, Boolean available) {
		return (root, query, cb) -> {
			List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

			if (genre != null && !genre.isBlank()) {
				predicates.add(cb.equal(cb.lower(root.get("genre")), genre.trim().toLowerCase()));
			}

			if (author != null && !author.isBlank()) {
				String a = "%" + author.trim().toLowerCase() + "%";
				predicates.add(cb.like(cb.lower(root.get("author")), a));
			}

			if (available != null && available) {
				predicates.add(cb.greaterThan(root.get("availableCopies"), 0));
			}

			return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
		};
	}
}

