package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Cart;
import org.acme.bookstore.entity.CartItem;
import org.acme.bookstore.entity.Order;
import org.acme.bookstore.entity.OrderItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderRepository {

    @Inject
    EntityManager em;
    BookRepository bookRepo;

    public List<Object[]> listOrdersWithCustomerNames() {
        return em.createQuery(
                "SELECT o.id, o.date, c.username " +
                        "FROM Order o JOIN Customer c " +
                        "ON o.userId = c.id " +
                        "order BY o.date DESC",
                Object[].class).getResultList();
    }

    public List<Object[]> listOrdersWithCustomerId(Long userId) {
        return em.createQuery(
                "SELECT o.id, o.date " +
                        "FROM Order o  " +
                        "ON o.userId = :userId " +
                        "order BY o.date DESC",
                Object[].class).setParameter("userId", userId).getResultList();
    }

    public List<Object[]> listOrderItemsWithBookTitles(Long orderId) {
        return em.createQuery(
                "SELECT b.id, b.title, oi.quantity " +
                        "FROM OrderItem oi, Book b " +
                        "WHERE oi.bookId = b.id AND oi.orderId = :oid",
                Object[].class).setParameter("oid", orderId).getResultList();
    }

    public Double totalRevenueForMonth(int year, int month) {
        Double res = em.createQuery(
            "SELECT sum(oi.quantity * b.price) " +
            "FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
            "JOIN Book b ON oi.bookId = b.id " +
            "WHERE FUNCTION('year', o.date) = :y " +
            "AND FUNCTION('month', o.date) = :m",
                Double.class).setParameter("y", year).setParameter("m", month).getSingleResult();
        if (res == null) {
            return 0d;
        } else {
            return res;
        }
    }

    public List<Object[]> top5BestsellingBooks() {
        return em.createQuery(
                "SELECT b.id, b.title, SUM(oi.quantity * b.price) AS revenue " +
                "FROM OrderItem oi, Book b " +
                "WHERE oi.bookId = b.id " +
                "GROUP BY b.id, b.title " +
                "ORDER BY revenue DESC",
                Object[].class).setMaxResults(5).getResultList();
    }

    public Object[] mostPopularGenre() {
        List<Object[]> result = em.createQuery(
                "SELECT b.genre, SUM(oi.quantity) AS total " +
                        "FROM OrderItem oi, Book b " +
                        "WHERE oi.bookId = b.id " +
                        "GROUP BY b.genre " +
                        "ORDER BY total DESC",
                Object[].class).setMaxResults(1).getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

      // Create order + items
    @Transactional
    public void createOrder(Order order, List<OrderItem> items) {
       em.persist(order);
       em.flush();  // ensure order.id is generated

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            em.persist(item);
        }
    }

    @Transactional
    public void addOrderItems(Order order, List<OrderItem> items) {
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            em.persist(item);
        }
    }

    // Update order + items
    @Transactional
    public void updateOrder(Order order, List<OrderItem> items) {
        em.merge(order);

        // Sil eski itemları
        em.createQuery("DELETE FROM OrderItem oi WHERE oi.orderId = :oid")
          .setParameter("oid", order.getId())
          .executeUpdate();

        // Ekle yeni itemları
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            em.persist(item);
        }
    }

    // Delete order + items
    @Transactional
    public boolean deleteOrder(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            em.createQuery("DELETE FROM OrderItem oi WHERE oi.orderId = :oid")
              .setParameter("oid", orderId)
              .executeUpdate();
            em.remove(order);
            return true;
        }
        return false;
    }

     public Order findById(Long orderId) {
        return em.find(Order.class, orderId);
    }

     public List<Order> listAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
     }

     @Transactional
    public void updateOrder(Order order) {
        em.merge(order);
    }

    public Order checkoutCart(Long cartId) {
        Long cartExists = em.createQuery(
                "SELECT COUNT(c) FROM Cart c WHERE c.id = :cartId", Long.class)
                .setParameter("cartId", cartId)
                .getSingleResult();
        if (cartExists == 0) {
            throw new IllegalArgumentException("Cart not found");
        }

        //get cart items
        List<CartItem> cartItems = em.createQuery(
            "SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId", CartItem.class)
            .setParameter("cartId", cartId)
            .getResultList();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Create new order
        Order order = new Order();

        order.setUserId(em.find(Cart.class, cartId).getUserId());

        List<OrderItem> orderItems = cartItems.stream()
        .map(ci -> new OrderItem(null, ci.getBookId(), ci.getQuantity()))
        .toList();

        //insert order items
        createOrder(order,orderItems);
    
        // Decrease stock
        for (CartItem item : cartItems) {
            bookRepo.decreaseStock(item.getBookId(), item.getQuantity());
        }

        //clear cart
        em.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
            .setParameter("cartId", cartId)
            .executeUpdate();

        return order;
    }

    public Long getTotalOfOrder(Long orderId){
        Long total = em.createQuery(
           "SELECT SUM(oi.quantity * b.price) " +
            "FROM OrderItem oi JOIN Book b ON oi.bookId = b.id " +
            "WHERE oi.orderId = :oid", Long.class)
            .setParameter("oid", orderId)
            .getSingleResult();
            
            return total;
    }
}


