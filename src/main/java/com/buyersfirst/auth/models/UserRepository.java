package com.buyersfirst.auth.models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Integer>{
    Users findByEmail(String email);

    List<Users> findUsersByEmail(String email);
}
