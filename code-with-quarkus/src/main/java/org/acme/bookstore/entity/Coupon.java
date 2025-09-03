package org.acme.bookstore.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // each coupon belongs to a customer

    @Column(name = "discount", nullable = false)
    private int discount;

    @Column(name = "exp_date")
    private LocalDate expDate;

    @Column(name = "isUsed")
    private boolean isUsed = false;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    public Coupon() {}

    public Coupon(Long customerId, int discount, LocalDate date, boolean used) {
        this.customerId = customerId;
        this.discount = discount;
        this.expDate = date;
        this.isUsed = used;
    }

    // getters and setters
    public Long getCouponId() { return coupon_id; }
    public void setCouponId(Long coupon_id) { this.coupon_id = coupon_id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public LocalDate getExpDate() { return expDate; }
    public void setExpDate(LocalDate expDate) { this.expDate = expDate; }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean isUsed) { this.isUsed = isUsed; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
