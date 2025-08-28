package org.acme.bookstore.api;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
// import org.acme.bookstore.entity.Book;
import org.acme.bookstore.entity.Publisher;
import org.acme.bookstore.repository.PublisherRepository;

import java.util.List;
@Path("/publishers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublisherResource {

    @Inject
    PublisherRepository publisherRepository;

    @GET
    public List<Publisher> listAll() {
        return publisherRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Publisher getPublisher(@PathParam("id") Long id) {
        return publisherRepository.findById(id);
    }

    @POST
    @Transactional
    public Response addPublisher(Publisher publisher) {
        publisherRepository.persist(publisher);
        return Response.status(Response.Status.CREATED).entity(publisher).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletePublisher(@PathParam("id") Long id) {
        Publisher publisher = publisherRepository.findById(id);
        if (publisher == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        publisherRepository.delete(publisher);
        return Response.noContent().build();
    }
}
