package org.acme.bookstore.repository;

import java.util.List;
import java.util.UUID;
import org.acme.bookstore.entity.Coupon;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CouponRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Coupon> listAll() {
        return em.createQuery("SELECT c FROM Coupon c", Coupon.class).getResultList();
    }

    public Coupon findById(Long id) {
        return em.find(Coupon.class, id);
    }

    public List<Coupon> listByCustomer(Long customerId) {
        return em.createQuery("SELECT c FROM Coupon c WHERE c.customerId = :customerId", Coupon.class)
                 .setParameter("customerId", customerId)
                 .getResultList();
    }

    public Coupon findByCode(String code) {
        List<Coupon> results = em.createQuery(
            "SELECT c FROM Coupon c WHERE c.code = :code", Coupon.class
        ).setParameter("code", code).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public Coupon findByCustomerAndCode(Long customerId, String code) {
        List<Coupon> results = em.createQuery(
            "SELECT c FROM Coupon c WHERE c.customerId = :customerId AND c.code = :code", 
            Coupon.class
        )
        .setParameter("customerId", customerId)
        .setParameter("code", code)
        .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void persist(Coupon coupon) {
        em.persist(coupon);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Coupon coupon = em.find(Coupon.class, id);
        if (coupon != null) {
            em.remove(coupon);
            return true;
        }
        return false;
    }

    @Transactional
    public Coupon update(Coupon coupon) {
        return em.merge(coupon);
    }

    public static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
