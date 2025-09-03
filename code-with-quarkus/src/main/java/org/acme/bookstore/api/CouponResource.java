package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.entity.Coupon;
import org.acme.bookstore.repository.CouponRepository;

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

@Path("/api/coupons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CouponResource {
    @Inject
    CouponRepository couponRepo;

    @GET
    public List<Coupon> getAllCoupons() {
        return couponRepo.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getCouponById(@PathParam("id") Long id) {
        Coupon coupon = couponRepo.findById(id);
        if (coupon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(coupon).build();
    }

    // @GET
    // @Path("/customer/{customer_id}")
    // public List<Coupon> getCouponsByCustomer(@PathParam("customer_id") long customer_id) {
    //     return couponRepo.listCouponsByCustomer(customer_id);
    // }

    @GET
    @Path("/{id}/discount")
    public Response getCouponDiscount(@PathParam("id") Long id) {
        Coupon coupon = couponRepo.findById(id);
        if (coupon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(coupon.getDiscount()).build();
    }

    @PUT
    @Path("/{id}/use")
    @Transactional
    public Response markCouponAsUsed(@PathParam("id") Long id) {
        Coupon coupon = couponRepo.findById(id);
        if (coupon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        coupon.setUsed(true);
        couponRepo.update(coupon);
        return Response.ok(coupon).build();
    }

    @POST
        @Transactional
        public Response createCoupon(Coupon coupon) {
            // Generate a unique code before persisting
            String code;
            do {
                code = CouponRepository.generateCode();
            } while (couponRepo.findByCode(code) != null); // ensure uniqueness
            coupon.setCode(code);

            couponRepo.persist(coupon);

            return Response.status(Response.Status.CREATED).entity(coupon).build();
        }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCoupon(@PathParam("id") Long id) {
        boolean deleted = couponRepo.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateCoupon(@PathParam("id") Long id, Coupon coupon) {
        Coupon existingCoupon = couponRepo.findById(id);
        if (existingCoupon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingCoupon.setDiscount(coupon.getDiscount());
        existingCoupon.setExpDate(coupon.getExpDate());
        existingCoupon.setUsed(coupon.isUsed());

        couponRepo.update(existingCoupon);

        return Response.ok(existingCoupon).build();
    }

@GET
@Path("/{id}/code")
public Response getCouponCode(@PathParam("id") Long id) {
    Coupon coupon = couponRepo.findById(id);
    if (coupon == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(coupon.getCode()).build();
}

@GET
@Path("/by-code/{code}/discount")
public Response getDiscountByCode(@PathParam("code") String code) {
    Coupon coupon = couponRepo.findByCode(code);
    if (coupon == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Invalid coupon code").build();
    }
    return Response.ok(coupon.getDiscount()).build();
}


@GET
@Path("/customer/{customerId}")
public List<Coupon> getCouponsByCustomer(@PathParam("customerId") Long customerId) {
    return couponRepo.listByCustomer(customerId);
}

@GET
@Path("/customer/{customerId}/code/{code}/discount")
public Response getCustomerCouponDiscount(@PathParam("customerId") Long customerId,
                                          @PathParam("code") String code) {
    Coupon coupon = couponRepo.findByCustomerAndCode(customerId, code);
    if (coupon == null) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity("Customer does not own this coupon code").build();
    }
    return Response.ok(coupon.getDiscount()).build();
}

}