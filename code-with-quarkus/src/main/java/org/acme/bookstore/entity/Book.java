package org.acme.bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {

    public enum BookFormat { PHYSICAL, EBOOK, AUDIOBOOK }
    public enum BookLang { ENGLISH, GERMAN, FRENCH, TURKISH, SPANISH }
    public enum Genre { POETRY, HORROR, SHORTSTORY, SCIFI, NONFICTIONAL, COOKING , HISTORY }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id") // column name
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private BookFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private BookLang language;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    @Column(name = "publish_date")
    private LocalDate date;

    @Column(name = "author")
    private String author;

    @Column(name = "price")
    private double price;

    @Column(name = "page_number")
    private int pageNumber;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "stock")
    private int stock = 0;

    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;

    public Book() {}

    public Book(String title, BookFormat format, BookLang language, LocalDate date, double price, int pageNumber, String isbn, int stock, Genre genre, Long publisherId, String author) {
    this.title = title;
    this.format = format;
    this.language = language;
    this.date = date;
    this.price = price;
    this.pageNumber = pageNumber;
    this.isbn = isbn;
    this.stock = stock;
    this.genre = genre;
    this.publisherId = publisherId;
    this.author = author;
}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BookFormat getFormat() { return format; }
    public void setFormat(BookFormat format) { this.format = format; }

    public BookLang getLanguage() { return language; }
    public void setLanguage(BookLang language) { this.language = language; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
