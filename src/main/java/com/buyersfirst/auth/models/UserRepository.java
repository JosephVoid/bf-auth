package com.buyersfirst.auth.models;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface UserRepository extends CrudRepository<Users, Integer> {
    Users findByEmail(String email);

    List<Users> findUsersByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
                UPDATE users SET password = COALESCE(:pass, password) WHERE users.email = :email
            """, nativeQuery = true)
    void updateUserPass(String email, String pass);
}
