package org.acme.bookstore.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.acme.bookstore.entity.Book;

import java.util.List;

@ApplicationScoped
public class BookRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> listBooksWithAuthors() {
        return em.createQuery(
            "SELECT b.title, a.name " +
            "FROM Book b JOIN Author a ON b.authorId = a.id", Object[].class
        ).getResultList();
    }

    public List<Object[]> listBooksWithPublisherNames() {
    return em.createQuery(
        "SELECT b.title, p.name " +
        "FROM Book b JOIN Publisher p ON b.publisherId = p.id", Object[].class
    ).getResultList();
}

public List<Book> listBooksByAuthor(long authorId) {
    return em.createQuery(
        "SELECT b FROM Book b WHERE b.authorId = :authorId", Book.class
    ).setParameter("authorId", authorId).getResultList();
}

public List<Book> listAudiobooks() {
    return em.createQuery(
        "SELECT b FROM Book b WHERE b.format = 'AUDIOBOOK'", Book.class
    ).getResultList();
}


//Since the genre is not a string, for hybernate to find the genre it should be made into a string
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
}
