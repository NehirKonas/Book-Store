package org.acme.bookstore.api;

import java.util.List;

import org.acme.bookstore.entity.Coupon;
import org.acme.bookstore.entity.Customer;
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

@Path("/coupons")
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
    // @Path("/customer/{customerId}")
    // public List<Coupon> getCouponsByCustomer(@PathParam("customerId") long customerId) {
    //     return couponRepo.listCouponsByCustomer(customerId);
    // }


    @POST
    @Transactional
    public Response createCoupon(Coupon coupon) {
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
}