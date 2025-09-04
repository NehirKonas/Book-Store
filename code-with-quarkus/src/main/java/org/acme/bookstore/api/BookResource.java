package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.entity.Book;
import org.acme.bookstore.repository.BookRepository;

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

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    BookRepository bookRepo;

    @GET
    @Path("/allBooks")
    public List<Book> getAllBooks() {
        return bookRepo.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        Book book = bookRepo.findById(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    @GET
    @Path("/with-authors")
    public List<Object[]> getBooksWithAuthors() {
        return bookRepo.listBooksWithAuthors();
    }

    @GET
    @Path("/with-publishers")
    public List<Object[]> getBooksWithPublishers() {
        return bookRepo.listBooksWithPublisherNames();
    }

    @GET
    @Path("/author/{authorId}")
    public List<Book> getBooksByAuthor(@PathParam("authorId") long authorId) {
        return bookRepo.listBooksByAuthor(authorId);
    }

    @GET
    @Path("/audiobooks")
    public List<Book> getAudiobooks() {
        return bookRepo.listAudiobooks();
    }

    @GET
    @Path("/average-price/{genre}")
    public Response getAveragePriceByGenre(@PathParam("genre") String genre) {
        Double avgPrice = bookRepo.findAveragePriceByGenre(genre);
        if (avgPrice == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No books found for genre: " + genre).build();
        }
        return Response.ok(avgPrice).build();
    }

    @POST
    @Transactional
    public Response createBook(Book book) {
        bookRepo.persist(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteBook(@PathParam("id") Long id) {
        boolean deleted = bookRepo.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateBook(@PathParam("id") Long id, Book book) {
        Book existingBook = bookRepo.findById(id);
        if (existingBook == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields
        existingBook.setTitle(book.getTitle());
        existingBook.setFormat(book.getFormat());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setGenre(book.getGenre());
        existingBook.setDate(book.getDate());
        existingBook.setPrice(book.getPrice());
        existingBook.setPageNumber(book.getPageNumber());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setStock(book.getStock());

        bookRepo.update(existingBook);

        return Response.ok(existingBook).build();
    }
}
