package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.entity.Cart;
import org.acme.bookstore.entity.CartItem;
import org.acme.bookstore.repository.CartRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {
    
    @Inject
    CartRepository cartRepo;
    
    @GET
    @Path("/{userId}")
    public Response getCartOfCustomer(@PathParam("userId") Long id){
        Cart cart = cartRepo.getCustomerCart(id);

        if(cart == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(cart).build();
    }

    @GET
    @Path("/{cartId}/items")
    public Response getCartItems(@PathParam("cartId") Long cartId) {
        List<CartItem> items = cartRepo.getCartItems(cartId);
        if (items.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(items).build();
    }

    @POST
    @Transactional
    public Response createCart(Cart cart){
        cartRepo.createCart(cart, List.of());
        return Response.status(Response.Status.CREATED).entity(cart).build();

    }
    @POST
    @Path("/{userId}/items")
    @Transactional
    public Response addItemCart(@PathParam("userId") Long id, CartItem item){
        Cart cart = cartRepo.getCustomerCart(id);
        if(cart == null){
            return Response.status(Response.Status.NOT_FOUND).build();

        }

        item.setCartId(id);

        cartRepo.addCartItems(cart, item);
        return Response.ok(item).build();

    }

    // Update order with items
    @PUT
    @Path("/{cartId}/items")
    @Transactional
    public Response updateCart(@PathParam("cartId") Long id, List<CartItem> items) {
        Cart cart = cartRepo.findById(id);
        if (cart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cart.setId(id);
        cartRepo.updateCart(cart, items);
        return Response.ok(cart).build();
    }
    
    // Delete  with items
    @DELETE
    @Path("/{id}/items")
    @Transactional
    public Response deleteOrder(@PathParam("id") Long id) {
        boolean deleted = cartRepo.emptyCart(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    // Delete spesific book from cart
    @DELETE
    @Path("/{userId}/items/{bookId}")
    @Transactional
    public Response deleteCartItem(@PathParam("userId") Long userId, @PathParam("bookId") Long bookId) {
        boolean deleted = cartRepo.rmBookFromCart(userId, bookId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @GET 
    @Path("/{userId}/items")
    public List<Object []> getOrderItemsWithBookTitles(@PathParam("userId") Long userId){
        return cartRepo.listCartItemsWithBookTitles(userId);
    }

    @GET 
    @Path("/{userId}/items/total")
    public Double getTotalOfCart(@PathParam("userId") Long userId){
        return cartRepo.getTotalOfCart(userId);
    }

}
