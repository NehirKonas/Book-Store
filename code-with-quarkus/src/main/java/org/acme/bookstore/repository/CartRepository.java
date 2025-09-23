package org.acme.bookstore.repository;

import java.util.List;

import org.acme.bookstore.entity.Cart;
import org.acme.bookstore.entity.CartItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CartRepository {

    @Inject
    EntityManager em;

    public List<Object[]> listCartItems(int cart_id) {
        return em.createQuery(
                "SELECT b.id, b.title, b.price, b.quantity" +
                        "FROM CartItem ci, Book b" +
                        "WHERE ci.bookId = b.id AND ci.cart_id = :cid",
                Object[].class).setParameter("cid", cart_id).getResultList();
    }

    // Create cart + items
    @Transactional
    public void createCart(Cart cart, List<CartItem> items) {
        em.persist(cart);
        em.flush(); // ensure order.id is generated

        for (CartItem item : items) {
            item.setId(cart.getId());
            em.persist(item);
        }
    }

    @Transactional
    public void addCartItems(Cart cart, CartItem item) {
        int stock = em.createQuery(
                "SELECT b.stock " +
                        "FROM Book b " +
                        "WHERE b.id = :bookId",
                int.class).setParameter("bookId", item.getBookId()).getSingleResult();

        if (item.getQuantity() > stock) {
            throw new IllegalStateException("Not enough stock for bookId=" + item.getBookId());
        } else {
            item.setId(null); // ensure it's treated as new
            item.setCartId(cart.getId()); // link to the correct cart
            em.persist(item); // now it works
        }

    }

    @Transactional
    public void updateCart(Cart cart, List<CartItem> items) {
        // Merge cart in case some fields changed
        em.merge(cart);

        // Delete old cart items
        em.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cid")
                .setParameter("cid", cart.getId())
                .executeUpdate();

        // Add new items
        for (CartItem item : items) {
            item.setCartId(cart.getId()); // only set cartId
            item.setId(null); // ensure Hibernate treats it as new
            em.persist(item);
        }
    }

    // empty cart items
    @Transactional
    public boolean emptyCart(Long userId) {
        Cart cart = getCustomerCart(userId); // find cart by userId
        if (cart != null) {
            // Delete all items in that cart
            em.createQuery("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
                    .setParameter("cartId", cart.getId())
                    .executeUpdate();

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

    public Cart getCustomerCart(Long id) {
        Cart cart = em.createQuery(
                "SELECT c FROM Cart c WHERE c.userId = :customerId", Cart.class)
                .setParameter("customerId", id)
                .getSingleResult();

        return cart;
    }

    // In your CartRepository
    public List<CartItem> getCartItems(Long userId) {
        return em.createQuery(
                "SELECT ci FROM CartItem ci WHERE ci.userId = :uid", CartItem.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long cartId, Long itemId, int quantity) {
        CartItem item = em.find(CartItem.class, itemId);
        if (item != null && item.getCartId().equals(cartId)) {
            item.setQuantity(quantity);
            // persist not needed; managed entity will auto-update at commit
            return item;
        }
        return null; // or throw exception
    }

    @Transactional
    public boolean removeCartItem(Long cartId, Long itemId) {
        CartItem item = em.find(CartItem.class, itemId);
        if (item != null && item.getCartId().equals(cartId)) {
            em.remove(item);
            return true;
        }
        return false;
    }

    public List<Object[]> listCartItemsWithBookTitles(Long userId) {
        return em.createQuery(
                "SELECT b.id, b.title,a.name, b.price, ci.quantity " +
                        "FROM CartItem ci JOIN Book b ON ci.bookId = b.id JOIN Cart c ON c.id = ci.cartId JOIN Author a ON a.id = b.authorId "
                        +
                        "WHERE c.userId = :uid",
                Object[].class).setParameter("uid", userId).getResultList();
    }

    public Double getTotalOfCart(Long userId) {
        Double total = em.createQuery(
                "SELECT SUM(ci.quantity * b.price) " +
                        "FROM CartItem ci JOIN Book b ON ci.bookId = b.id JOIN Cart c ON c.id = ci.cartId " +
                        "WHERE c.userId = :uid",
                Double.class)
                .setParameter("uid", userId)
                .getSingleResult();

        return total != null ? total : 0.0;
    }

    @Transactional
    public boolean rmBookFromCart(Long userId, Long bookId) {
        Long cartId = em.createQuery("SELECT c.id FROM Cart c WHERE c.userId = :uid", Long.class)
                .setParameter("uid", userId)
                .getSingleResult();

        int deletedRows = em.createQuery(
                "DELETE FROM CartItem ci WHERE ci.bookId = :bid AND ci.cartId = :cid")
                .setParameter("bid", bookId)
                .setParameter("cid", cartId)
                .executeUpdate();

        return deletedRows > 0;
    }

    @Transactional
    public Response incrementCartItem(Long userId, Long bookId) {
        CartItem item = em.createQuery(
                "SELECT ci FROM CartItem ci JOIN Cart c ON ci.cartId = c.id WHERE ci.bookId = :bid AND c.userId = :uid",
                CartItem.class)
                .setParameter("uid", userId)
                .setParameter("bid", bookId)
                .getSingleResult();

        if (item == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        int stock = em.createQuery(
                "SELECT b.stock " +
                        "FROM Book b " +
                        "WHERE b.id = :bookId",
                int.class).setParameter("bookId", item.getBookId()).getSingleResult();

        if (stock < item.getQuantity() + 1)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        item.setQuantity(item.getQuantity() + 1);
        em.merge(item);
        return Response.ok(item).build();
    }

    @Transactional
    public Response decrementCartItem(Long userId, Long bookId) {
        CartItem item = em.createQuery(
                "SELECT ci FROM CartItem ci JOIN Cart c ON ci.cartId = c.id WHERE ci.bookId = :bid AND c.userId = :uid",
                CartItem.class)
                .setParameter("uid", userId)
                .setParameter("bid", bookId)
                .getSingleResult();

        if (item == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            em.merge(item);
        }

        return Response.ok(item).build();
    }

}