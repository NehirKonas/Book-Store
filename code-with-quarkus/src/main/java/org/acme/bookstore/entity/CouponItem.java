package org.acme.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupon_item")
public class CouponItem {
    
    @Column(name ="coupon_id")
    private long coupon_id;

    @Column(name = "user_id")
    private long user_id;

    public CouponItem(){}

    public CouponItem(long coupon_id, long user_id){
        this.coupon_id = coupon_id;
        this.user_id = user_id;
    }

    // Getter and Setter for coupon_id
    public long getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(long coupon_id) {
        this.coupon_id = coupon_id;
    }

    // Getter and Setter for user_id
    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    
}
