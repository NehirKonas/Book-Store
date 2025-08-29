package org.acme.bookstore.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class OrderRepository {

    @Inject
    EntityManager em;

    public List<Object[]> listOrdersWithCustomerNames() {
        return em.createQuery(
                "SELECT o.id, o.orderDate, u.username " +
                        "FROM Order o JOIN o.user u " +
                        "order BY o.orderDate DESC",
                Object[].class).getResultList();
    }

    public List<Object[]> listOrderItemsWithBookTitles(Long orderId) {
        return em.createQuery(
                "SELECT b.id, b.title, oi.quantity " +
                        "FROM OrderItem oi JOIN oi.book b " +
                        "WHERE oi.order.id = :oid",
                Object[].class).setParameter("oid", orderId).getResultList();
    }

    public Double totalRevenueForMonth(int year, int month) {
        Double res = em.createQuery(
                "SELECT sum(oi.quantity * b.price) " +
                        "FROM OrderItem oi JOIN oi.order o JOIN oi.book b " +
                        "WHERE FUNCTION('year', o.orderDate) = :y " +
                        "AND FUNCTION('month', o.orderDate) = :m",
                Double.class).setParameter("y", year).setParameter("m", month).getSingleResult();
        if (res == null) {
            return 0d;
        } else {
            return res;
        }
    }

    public List<Object[]> top5BestsellingBooks() {
        return em.createQuery(
                "SELECT b.id, b.title, SUM(oi.quantity*b.price) AS revenue " +
                        "FROM OrderItem oi JOIN oi.book b " +
                        "GROUP BY b.id, b.title " +
                        "ORDER BY revenue DESC",
                Object[].class).setMaxResults(5).getResultList();
    }

    public Object[] mostPopularGenre() {
        List<Object[]> result = em.createQuery(
                "SELECT b.genre, SUM(oi.quantity) AS total " +
                        "FROM OrderItem oi JOIN oi.book b " +
                        "GROUP BY b.genre " +
                        "ORDER BY total DESC",
                Object[].class).setMaxResults(1).getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }
}


