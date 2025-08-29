package org.acme.bookstore.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "coupon")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_id;

    @Column(name = "discount" ,nullable= false)
    private int discount;

    @Column(name = "exp_date")
    private LocalDate expDate;

    @Column(name="isUsed")
    private boolean isUsed = false;

    public Coupon(){}

    public Coupon(int discount, LocalDate date, boolean used){
        this.discount = discount;
        this.expDate =  date;
        this.isUsed = used;
    }
    
    //getters and setters
    public Long getCupon_id() {
        return coupon_id;
    }

    public void setCupon_id(Long cupon_id) {
        this.coupon_id = cupon_id;
    }

    // Getter and Setter for discount
    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    // Getter and Setter for expDate
    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    // Getter and Setter for isUsed
    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

}
