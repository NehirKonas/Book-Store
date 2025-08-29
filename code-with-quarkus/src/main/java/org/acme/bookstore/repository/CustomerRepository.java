package org.acme.bookstore.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.acme.bookstore.entity.Customer;

import java.util.List;

@ApplicationScoped
public class CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> listCustomersWithDisc() {
        return em.createQuery(
            "SELECT c.username, c.disc FROM Customer c", Object[].class
        ).getResultList();
    }

    public List<Customer> listAll() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    @Transactional
    public void persist(Customer customer) {
        em.persist(customer);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            em.remove(customer);
            return true;
        }
        return false;
    }

    @Transactional
    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    @Transactional
    public Customer updateDisc(Long customerId, int newDisc) {
    Customer customer = em.find(Customer.class, customerId);
    if (customer != null) {
        if (newDisc < 5) newDisc = 5;
        if (newDisc > 50) newDisc = 50;
        customer.setDisc(newDisc);
        em.merge(customer);
    }
    return customer;
}

}
