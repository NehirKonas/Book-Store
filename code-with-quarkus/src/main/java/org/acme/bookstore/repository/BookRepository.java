package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Author;
import org.acme.bookstore.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class BookRepository {

    @PersistenceContext
    private EntityManager em;

    // returns the author's name or null if not found
    public String findAuthorNameById(Long id) {
        return em.createQuery("SELECT a.name FROM Author a WHERE a.id = :id", String.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    // returns the publisher's name or null if not found
    public String findPublisherNameById(Long id) {
        return em.createQuery("SELECT p.name FROM Publisher p WHERE p.id = :id", String.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Book> listAllWithAuthorNames() {
        // Use JOIN query to get books with author names in one query
        List<Object[]> results = em.createQuery(
                "SELECT b, a.name " +
                        "FROM Book b LEFT JOIN Author a ON a.id = b.authorId " +
                        "ORDER BY b.id",
                Object[].class)
                .getResultList();
        // Map results to Book objects with author names set
        return results.stream()
                .map(row -> {
                    Book book = (Book) row[0]; // First element is the Book
                    book.setAuthorName((String) row[1]); // Second element is the author name
                    return book;
                })
                .toList();
    }

    public List<Object[]> listBooksWithAuthors() {
        return em.createQuery(
                "SELECT b.title, a.name " +
                        "FROM Book b JOIN Author a ON b.authorId = a.id",
                Object[].class).getResultList();
    }

    public List<Object[]> listBooksWithPublisherNames() {
        return em.createQuery(
                "SELECT b.title, p.name " +
                        "FROM Book b JOIN Publisher p ON b.publisherId = p.id",
                Object[].class).getResultList();
    }

    public List<Book> listBooksByAuthor(long authorId) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.authorId = :authorId", Book.class).setParameter("authorId", authorId)
                .getResultList();
    }

    public List<Book> listAudiobooks() {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.format = 'AUDIOBOOK'", Book.class).getResultList();
    }

    // Since the genre is not a string, for hybernate to find the genre it should be
    // made into a string
    public Double findAveragePriceByGenre(String genre) {
        Book.Genre enumGenre = Book.Genre.valueOf(genre.toUpperCase());
        return em.createQuery(
                "SELECT AVG(b.price) FROM Book b WHERE b.genre = :genre", Double.class)
                .setParameter("genre", enumGenre)
                .getSingleResult();
    }

    public List<Book> listAll() {
        return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }

    public Book findById(Long id) {
        return em.find(Book.class, id);
    }

    @Transactional
    public void persist(Book book) {
        em.persist(book);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
            return true;
        }
        return false;
    }

    @Transactional
    public Book update(Book book) {
        return em.merge(book);
    }

    @Transactional
    public void decreaseStock(Long bookId, int amount) {
        Book book = em.find(Book.class, bookId);
        if (book.getStock() < amount) {
            throw new IllegalArgumentException("Not enough stock");
        }

        // Decrease stock
        book.setStock(book.getStock() - amount);

        // Persist the change
        em.merge(book);
    }
}
