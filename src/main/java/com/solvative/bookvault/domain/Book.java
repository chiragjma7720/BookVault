package com.solvative.bookvault.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "books")
public class Book {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "isbn", nullable = false, unique = true, length = 32)
	private String isbn;

	@Column(name = "title", nullable = false, length = 200)
	private String title;

	@Column(name = "author", nullable = false, length = 200)
	private String author;

	@Column(name = "genre", nullable = false, length = 100)
	private String genre;

	@Column(name = "total_copies", nullable = false)
	private int totalCopies;

	@Column(name = "available_copies", nullable = false)
	private int availableCopies;

	public Book() {
	}

	public Book(UUID id, String isbn, String title, String author, String genre, int totalCopies, int availableCopies) {
		this.id = id;
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.genre = genre;
		this.totalCopies = totalCopies;
		this.availableCopies = availableCopies;
	}

	public UUID getId() {
		return id;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getGenre() {
		return genre;
	}

	public int getTotalCopies() {
		return totalCopies;
	}

	public int getAvailableCopies() {
		return availableCopies;
	}

	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void borrowOne() {
		this.availableCopies = this.availableCopies - 1;
	}

	public void returnOne() {
		this.availableCopies = this.availableCopies + 1;
	}
}

