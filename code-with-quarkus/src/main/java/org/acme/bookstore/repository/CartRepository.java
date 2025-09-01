package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Cart;
import org.acme.bookstore.entity.CartItem;
import org.acme.bookstore.entity.Order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CartRepository {
    
    @Inject
    EntityManager em;

    public List<Object []> listCartItems(int cart_id){
        return em.createQuery(
            "SELECT b.id, b.title, b.price, b.quantity" +
            "FROM CartItem ci, Book b"+
            "WHERE ci.bookId = b.id AND ci.cart_id = :cid",
            Object[].class).setParameter("cid", cart_id).getResultList();
    }

      // Create cart + items
    @Transactional
    public void createCart(Cart cart, List<CartItem> items) {
       em.persist(cart);
       em.flush();  // ensure order.id is generated

        for (CartItem item : items) {
            item.setId(cart.getId());
            em.persist(item);
        }
    }

    @Transactional
    public void addCartItems(Cart cart, List<CartItem> items) {
        for (CartItem item : items) {
            item.setId(cart.getId());
            em.persist(item);
        }
    }

    // Update cart + items
    @Transactional
    public void updateCart(Cart cart, List<CartItem> items) {
        em.merge(cart);

        // Sil eski itemları
        em.createQuery("DELETE FROM OrderItem oi WHERE oi.orderId = :oid")
          .setParameter("oid", cart.getId())
          .executeUpdate();

        // Ekle yeni itemları
        for (CartItem item : items) {
            item.setId(cart.getId());
            em.persist(item);
        }
    }

    // empty cart items
    @Transactional
    public boolean emptyCart(Long cartId) {
        Order cart = em.find(Order.class, cartId);
        if (cart != null) {
            em.createQuery("DELETE FROM CartItem ci WHERE ci.orderId = :cid")
              .setParameter("oid", cartId)
              .executeUpdate();
            em.remove(cart);
            return true;
        }
        return false;
    }

     public Cart findById(Long cartId) {
        return em.find(Cart.class, cartId);
    }

     public List<Cart> listAll() {
        return em.createQuery("SELECT c FROM Cart c", Cart.class).getResultList();
     }

     @Transactional
    public void updateOrder(Cart cart) {
        em.merge(cart);
    }

    public Cart getCustomerCart(Long id){
       Cart cart = em.createQuery(
        "SELECT c FROM Cart c WHERE c.id = :customerId", Cart.class)
                .setParameter("customerId", id)
                .getSingleResult();

        return cart;
    }

    
}
