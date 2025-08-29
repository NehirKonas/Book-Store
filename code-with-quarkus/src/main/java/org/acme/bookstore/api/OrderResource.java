package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.repository.OrderRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject 
    OrderRepository orderRepo;

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
}
