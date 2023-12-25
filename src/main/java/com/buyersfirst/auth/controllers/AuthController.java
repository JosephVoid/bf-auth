package com.buyersfirst.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.buyersfirst.auth.interfaces.TokenRequest;
import com.buyersfirst.auth.models.UserRepository;
import com.buyersfirst.auth.models.Users;
import com.buyersfirst.auth.services.JWTBuilder;

import jakarta.persistence.PersistenceException;

@Controller
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTBuilder jwtBuilder;

    @PostMapping("/")
    public @ResponseBody String getToken (@RequestBody TokenRequest auth) {
        try {
            Users user = userRepository.findByEmail(auth.email);
            String hashedPass = new BCryptPasswordEncoder(10).encode(auth.password);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
            if (user.getPassword().equals(hashedPass)) {
                return jwtBuilder.generateToken(Integer.toString(user.getId()), "USER");
            } 
            else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
            }
        } catch (PersistenceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password incorrect");
        }
    }

}
