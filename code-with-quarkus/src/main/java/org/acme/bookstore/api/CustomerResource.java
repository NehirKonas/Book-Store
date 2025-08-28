package org.acme.bookstore.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.bookstore.entity.Customer;
import org.acme.bookstore.repository.CustomerRepository;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerRepository customerRepository;

    // Create customer
    @POST
    public Response createCustomer(Customer customer) {
        customerRepository.persist(customer);
        return Response.ok(customer).status(201).build();
    }

    @GET
    public Response listAllCustomers() {
        List<Customer> customers = customerRepository.listAll();
        return Response.ok(customers).build();
    }

    @PUT
    @Path("/{id}/disc")
    public Response updateDisc(@PathParam("id") Long id, @QueryParam("newDisc") int newDisc) {
        Customer updated = customerRepository.updateDisc(id, newDisc);
        if (updated == null) {
            return Response.status(404).build();
        }
        return Response.ok(updated).build();
    }

    @GET
    @Path("/with-disc")
    public Response listWithDisc() {
        return Response.ok(customerRepository.listCustomersWithDisc()).build();
    }
}
