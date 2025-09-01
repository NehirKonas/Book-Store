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

@Path("/carts")
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


    @POST
    @Transactional
    public Response createCart(Cart cart){
        cartRepo.createCart(cart, List.of());
        return Response.status(Response.Status.CREATED).entity(cart).build();

    }
    @POST
    @Path("/{userId}")
    @Transactional
    public Response addItemsToCart(@PathParam("userId") Long id, List<CartItem> items){
        Cart cart = cartRepo.getCustomerCart(id);
        if(cart == null){
            return Response.status(Response.Status.NOT_FOUND).build();

        }

        for(CartItem item: items){
            item.setCartId(id);
        }

        cartRepo.addCartItems(cart, items);
        return Response.ok(items).build();

    }

    // Update order with items
    @PUT
    @Path("/{orderId}/items")
    @Transactional
    public Response updateOrder(@PathParam("orderId") Long id, List<CartItem> items) {
        Cart cart = cartRepo.findById(id);
        if (cart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cart.setId(id);
        cartRepo.updateCart(cart, items);
        return Response.ok(cart).build();
    }
    
    // Delete order with items
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteOrder(@PathParam("id") Long id) {
        boolean deleted = cartRepo.emptyCart(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }



}
