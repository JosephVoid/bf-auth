package com.buyersfirst.auth.models;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
    public Users(UUID id, String first_name, String last_name, String email, String password, String picture,
            String description, String phone, Timestamp joinedOn) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.description = description;
        this.phone = phone;
        JoinedOn = joinedOn;
    }

    @Id
    @Column(name = "id", updatable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    public UUID getUuid() {
        return id;
    }

    public void setUuid(UUID uuid) {
        id = uuid;
    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

    private String first_name;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @Column(nullable = true)
    private String last_name;

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = true)
    private String picture;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Column(nullable = true)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String phone;

    public Users() {
    }

    public Users(String first_name, String last_name, String email, String password, String picture, String description,
            String phone, Timestamp JoinedOn, String affCode) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.description = description;
        this.phone = phone;
        this.JoinedOn = JoinedOn;
        this.affiliate = affCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "joined_on")
    private Timestamp JoinedOn;

    public Timestamp getJoinedOn() {
        return JoinedOn;
    }

    public void setJoinedOn(Timestamp joinedOn) {
        JoinedOn = joinedOn;
    }

    @Column(nullable = true)
    private String affiliate;

    public String getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(String affiliate) {
        this.affiliate = affiliate;
    }
}
