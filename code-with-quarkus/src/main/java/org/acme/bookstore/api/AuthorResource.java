package org.acme.bookstore.api;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
// import org.acme.bookstore.entity.Book;
import org.acme.bookstore.entity.Author;
import org.acme.bookstore.repository.AuthorRepository;

import java.util.List;
@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @Inject
    AuthorRepository authorRepository;

    @GET
    public List<Author> listAll() {
        return authorRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Author getAuthor(@PathParam("id") Long id) {
        return authorRepository.findById(id);
    }

    @POST
    @Transactional
    public Response addAuthor(Author author) {
        authorRepository.persist(author);
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteAuthor(@PathParam("id") Long id) {
        Author author = authorRepository.findById(id);
        if (author == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        authorRepository.delete(author);
        return Response.noContent().build();
    }
}
