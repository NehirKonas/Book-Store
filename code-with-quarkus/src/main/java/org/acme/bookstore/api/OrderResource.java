package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.entity.Order;
import org.acme.bookstore.entity.OrderItem;
import org.acme.bookstore.repository.OrderRepository;

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

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject 
    OrderRepository orderRepo;

  
    @GET
    public List<Order> getAllOrders() {
        return orderRepo.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Order order = orderRepo.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @GET
    @Path("/customers")
    public List<Object []> listOrdersWithCustomerNames(){
        return orderRepo.listOrdersWithCustomerNames();
    }

    @GET 
    @Path("/{orderId}/items")
    public List<Object []> getOrderItemsWithBookTitles(@PathParam("orderId") Long orderId){
        return orderRepo.listOrderItemsWithBookTitles(orderId);
    }

    @GET
    @Path("/revenue/{year}/{month}")
    public Response getTotalRevenueForMonth(@PathParam("year") int year, @PathParam("month") int month) {
        Double revenue = orderRepo.totalRevenueForMonth(year, month);
        return Response.ok(revenue).build();
    }

    @GET
    @Path("/top5-books")
    public List<Object []> getTop5Books(){
        return orderRepo.top5BestsellingBooks();
    }

    @GET
    @Path("/popular-genre")
    public Response getMostPopularGenre() {
        Object[] result = orderRepo.mostPopularGenre();
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data found").build();
        }
        return Response.ok(result).build();
    }

    // Create order (initially empty items)
    @POST
    @Transactional
    public Response createOrder(Order order) {
        orderRepo.createOrder(order, List.of());
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    // Add items to an existing order
    @POST
    @Path("/{orderId}/items")
    @Transactional
    public Response addItemsToOrder(@PathParam("orderId") Long orderId, List<OrderItem> items) {
        Order order = orderRepo.findById(orderId);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // assign orderId to items
        for (OrderItem item : items) {
            item.setOrderId(orderId);
        }

        orderRepo.addOrderItems(order, items); // orderRepo handles item persistence
        return Response.ok(items).build();
    }
    
    // Update order with items
    @PUT
    @Path("/{orderId}")
    @Transactional
    public Response updateOrder(@PathParam("orderId") Long id, List<OrderItem> items) {
        Order order = orderRepo.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        order.setId(id);
        orderRepo.updateOrder(order, items);
        return Response.ok(order).build();
    }
    
    // Delete order with items
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteOrder(@PathParam("id") Long id) {
        boolean deleted = orderRepo.deleteOrder(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }


}
