package org.acme.bookstore.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.acme.bookstore.entity.Author;

import java.util.List;

@ApplicationScoped
public class AuthorRepository {

    @PersistenceContext
    EntityManager em;

    public List<Author> listAll() {
        return em.createQuery("SELECT p FROM Author p", Author.class).getResultList();
    }

    @Transactional
    public void persist(Author author) {
        em.persist(author);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Author author = em.find(Author.class, id);
        if (author != null) {
            em.remove(author);
            return true;
        }
        return false;
    }

    public Author findById(Long id) {
        return em.find(Author.class, id);
    }
    @Transactional
public void delete(Author author) {
    if (!em.contains(author)) {
        author = em.merge(author);
    }
    em.remove(author);
}

}
