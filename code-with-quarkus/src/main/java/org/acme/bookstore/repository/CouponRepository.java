package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Coupon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped

public class CouponRepository {
        @PersistenceContext
    private EntityManager em;


// public List<Coupon> listCouponsByCustomer(long customerId) {
//     return em.createQuery(
//         "SELECT c FROM Coupon c WHERE c.customerId = :customerId", Coupon.class
//     ).setParameter("customerId", customerId).getResultList();
// }



    public List<Coupon> listAll() {
        return em.createQuery("SELECT c FROM Coupon c", Coupon.class).getResultList();
    }

    public Coupon findById(Long id) {
        return em.find(Coupon.class, id);
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
}