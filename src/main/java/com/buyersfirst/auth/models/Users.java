package com.buyersfirst.auth.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "users")
public class Users {
    public Users(Integer id, String first_name, String last_name, String email, String password, String picture,
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
            String phone, Timestamp JoinedOn) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.description = description;
        this.phone = phone;
        this.JoinedOn = JoinedOn;
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
}
