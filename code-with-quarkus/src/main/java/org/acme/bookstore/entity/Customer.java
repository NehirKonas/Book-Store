package org.acme.bookstore.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_logged")
    private boolean isLogged;

    @Column(name = "disc")
    private int disc; // new discount field

    public Customer() {
        this.disc = 5; // default minimum discount
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Customer(String username, String email, String password, String phoneNumber, LocalDate birthDate, boolean isLogged, int disc) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.isLogged = isLogged;
        setDisc(disc); // ensure it's in range
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.length() < 5) {
            throw new IllegalArgumentException("Username cannot be null and must be at least 5 characters");
        }
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.matches("^[a-z0-9]+@gmail\\.com$")) {
            throw new IllegalArgumentException("Email must be lowercase, no special characters, and end with @gmail.com");
        }
        this.email = email;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password cannot be null and must be at least 6 characters");
        }
        this.password = password;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public boolean isLogged() { return isLogged; }
    public void setLogged(boolean logged) { isLogged = logged; }

    public int getDisc() { return disc; }
    public void setDisc(int disc) {
        if (disc < 5 || disc > 50) {
            throw new IllegalArgumentException("Disc must be between 5 and 50");
        }
        this.disc = disc;
    }
}
