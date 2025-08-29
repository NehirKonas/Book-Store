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

    public List<Object[]> findBooksWithPublisherNames() {
        return em.createQuery(
            "SELECT b.title, p.name " +
            "FROM Book b JOIN Publisher p ON b.publisherId = p.id",
            Object[].class
        ).getResultList();
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
