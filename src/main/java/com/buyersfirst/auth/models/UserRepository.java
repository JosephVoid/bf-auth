package com.buyersfirst.auth.models;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Integer>{
    Users findByEmail(String email);
}
