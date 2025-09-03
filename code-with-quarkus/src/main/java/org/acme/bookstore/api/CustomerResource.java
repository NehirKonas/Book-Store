package org.acme.bookstore.api;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.acme.bookstore.entity.Customer;
import org.acme.bookstore.repository.CustomerRepository;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerRepository customerRepository;

    // DTOs
    public static class CreateCustomerRequest {
        public String email;
        public String password;
        public String firstName;
        public String lastName;
        public String phone;
        public String address;
        public String birthDate; // YYYY-MM-DD format
    }

    public static class CustomerDto {
        public Long id;
        public String email;
        public String firstName;
        public String lastName;
        public String phone;
        public String address;
        public String birthDate; // YYYY-MM-DD

        public static CustomerDto from(Customer c) {
            CustomerDto d = new CustomerDto();
            d.id = c.getId();
            d.email = c.getEmail();
            d.firstName = c.getFirstName();
            d.lastName = c.getLastName();
            d.phone = c.getPhone();
            d.address = c.getAddress();
            d.birthDate = c.getBirthDate() != null ? c.getBirthDate().toString() : null;
            return d;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class LoginResponse {
        public Long id;
        public Long customerId;
        public String email;
        public String firstName;
        public String lastName;

        public LoginResponse(Customer c) {
            this.id = c.getId();
            this.customerId = c.getId();
            this.email = c.getEmail();
            this.firstName = c.getFirstName();
            this.lastName = c.getLastName();
        }
    }

    public static class UpdateProfileRequest {
        public String firstName;
        public String lastName;
        public String phone;
        public String address;
        public String birthDate; // YYYY-MM-DD format
    }

    public static class ChangePasswordRequest {
        public Long userId;
        public String currentPassword;
        public String newPassword;
    }

    public static class ErrorMessage {
        public String message;

        public ErrorMessage() {
        }

        public ErrorMessage(String message) {
            this.message = message;
        }
    }

    // Login method
    @POST
    @Path("/login")
    public Response login(LoginRequest req) {
        if (req == null || isBlank(req.email) || isBlank(req.password)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("email & password required"))
                    .build();
        }

        final String email = req.email.trim().toLowerCase();
        Customer customer = customerRepository.findByEmail(email); // ← new repo method
        if (customer == null || !safeEquals(customer.getPassword(), req.password)) {
            // same message to avoid user enumeration
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage("invalid credentials"))
                    .build();
        }

        return Response.ok(new LoginResponse(customer)).build();
    }

    // Create customer
    @POST
    @Path("/register")
    public Response createCustomer(CreateCustomerRequest request) {
        if (request == null || isBlank(request.email) || isBlank(request.password)) {
            return Response.status(400)
                    .entity(new ErrorMessage("Email and password are required"))
                    .build();
        }

        // Check if email already exists
        if (customerRepository.emailExists(request.email)) {
            return Response.status(409)
                    .entity(new ErrorMessage("Email already registered"))
                    .build();
        }

        try {
            Customer customer = new Customer();
            customer.setEmail(request.email);
            customer.setPassword(request.password);
            customer.setFirstName(trimToNull(request.firstName));
            customer.setLastName(trimToNull(request.lastName));
            customer.setPhone(trimToNull(request.phone));
            customer.setAddress(trimToNull(request.address));

            // Parse birth date if provided
            if (!isBlank(request.birthDate)) {
                customer.setBirthDate(LocalDate.parse(request.birthDate.trim()));
            }

            customerRepository.persist(customer);
            return Response.status(201).entity(customer).build();

        } catch (DateTimeParseException e) {
            return Response.status(400)
                    .entity(new ErrorMessage("Invalid birthDate format, expected YYYY-MM-DD"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    // Get all customers
    @GET
    public Response listAllCustomers() {
        List<Customer> customers = customerRepository.listAll();
        return Response.ok(customers).build();
    }

    // Get customer by ID
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            return Response.status(404)
                    .entity(new ErrorMessage("Customer not found"))
                    .build();
        }
        return Response.ok(customer).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateProfile(@PathParam("id") Long id, UpdateProfileRequest req) {
        Customer c = customerRepository.findById(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("customer not found"))
                    .build();
        }

        if (req.firstName != null)
            c.setFirstName(req.firstName.trim());
        if (req.lastName != null)
            c.setLastName(req.lastName.trim());
        if (req.phone != null)
            c.setPhone(req.phone.trim());
        if (req.address != null)
            c.setAddress(req.address.trim());

        if (req.birthDate != null) {
            if (req.birthDate.isBlank()) {
                c.setBirthDate(null);
            } else {
                try {
                    // İlk 10 karakter (YYYY-MM-DD)
                    String d = req.birthDate.length() >= 10 ? req.birthDate.substring(0, 10) : req.birthDate;
                    c.setBirthDate(LocalDate.parse(d));
                } catch (DateTimeParseException ex) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorMessage("birthDate must be YYYY-MM-DD"))
                            .build();
                }
            }
        }

        customerRepository.persist(c);
        return Response.ok(CustomerDto.from(c)).build();
    }

    @POST
    @Path("/change-password")
    @Transactional
    public Response changePassword(ChangePasswordRequest req) {
        if (req == null || req.userId == null ||
                req.currentPassword == null || req.newPassword == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("userId, currentPassword, newPassword required"))
                    .build();
        }
        if (req.newPassword.length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("newPassword must be at least 6 characters"))
                    .build();
        }

        Customer c = customerRepository.findById(req.userId);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("customer not found"))
                    .build();
        }

        // DEMO: düz karşılaştırma (üretimde HASH kullan!)
        if (!req.currentPassword.equals(c.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("wrong current password"))
                    .build();
        }

        c.setPassword(req.newPassword);
        customerRepository.persist(c);

        return Response.ok(new ErrorMessage("password updated")).build();
    }

    // Update discount
    @PUT
    @Path("/{id}/discount")
    public Response updateDiscount(@PathParam("id") Long id, @QueryParam("newDisc") int newDisc) {
        Customer updated = customerRepository.updateDisc(id, newDisc);
        if (updated == null) {
            return Response.status(404)
                    .entity(new ErrorMessage("Customer not found"))
                    .build();
        }
        return Response.ok(updated).build();
    }

    // Get customers with discount info
    @GET
    @Path("/with-discount")
    public Response listWithDiscount() {
        return Response.ok(customerRepository.listCustomersWithDisc()).build();
    }

    // Delete customer
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        boolean deleted = customerRepository.deleteById(id);
        if (!deleted) {
            return Response.status(404)
                    .entity(new ErrorMessage("Customer not found"))
                    .build();
        }
        return Response.ok().entity(new ErrorMessage("Customer deleted successfully")).build();
    }

    // Helper methods
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trimToNull(String s) {
        if (s == null)
            return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean safeEquals(String a, String b) {
        return a != null && a.equals(b);
    }
}