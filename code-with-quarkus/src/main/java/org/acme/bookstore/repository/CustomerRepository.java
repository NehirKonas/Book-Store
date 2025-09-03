package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Customer> listAll() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    public Customer findByEmail(String email) {
        List<Customer> list = em.createQuery(
                "SELECT c FROM Customer c WHERE LOWER(c.email) = :email", Customer.class)
                .setParameter("email", email.toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
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
    public Customer updateProfile(Long customerId, String firstName, String lastName,
            String phone, String address) {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            if (firstName != null)
                customer.setFirstName(firstName.trim());
            if (lastName != null)
                customer.setLastName(lastName.trim());
            if (phone != null)
                customer.setPhone(phone.trim());
            if (address != null)
                customer.setAddress(address.trim());
            em.merge(customer);
        }
        return customer;
    }

    @Transactional
    public Customer changePassword(Long customerId, String newPassword) {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            customer.setPassword(newPassword);
            em.merge(customer);
        }
        return customer;
    }

    @Transactional
    public Customer updateDisc(Long customerId, int newDisc) {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null) {
            if (newDisc < 5)
                newDisc = 5;
            if (newDisc > 50)
                newDisc = 50;
            customer.setDisc(newDisc);
            em.merge(customer);
        }
        return customer;
    }

    public List<Object[]> listCustomersWithDisc() {
        return em.createQuery(
                "SELECT c.firstName, c.lastName, c.email, c.disc FROM Customer c", Object[].class).getResultList();
    }

    public boolean emailExists(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE LOWER(c.email) = :email", Long.class)
                .setParameter("email", email.toLowerCase())
                .getSingleResult();
        return count > 0;
    }
}