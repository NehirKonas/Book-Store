package org.acme.bookstore.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.acme.bookstore.entity.Publisher;

import java.util.List;

@ApplicationScoped
public class PublisherRepository {

    @PersistenceContext
    EntityManager em;

    public List<Publisher> listAll() {
        return em.createQuery("SELECT p FROM Publisher p", Publisher.class).getResultList();
    }

    @Transactional
    public void persist(Publisher publisher) {
        em.persist(publisher);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Publisher publisher = em.find(Publisher.class, id);
        if (publisher != null) {
            em.remove(publisher);
            return true;
        }
        return false;
    }

    public Publisher findById(Long id) {
        return em.find(Publisher.class, id);
    }
    @Transactional
public void delete(Publisher publisher) {
    if (!em.contains(publisher)) {
        publisher = em.merge(publisher);
    }
    em.remove(publisher);
}

}
