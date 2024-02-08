package com.buyersfirst.auth.controllers;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.buyersfirst.auth.interfaces.SignUpRequest;
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
    public @ResponseBody String getToken(@RequestBody TokenRequest auth) {
        try {
            Users user = userRepository.findByEmail(auth.email);
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
            if (bCryptPasswordEncoder.matches(auth.password, user.getPassword())) {
                return jwtBuilder.generateToken(Integer.toString(user.getId()), "USER");
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
            }
        } catch (PersistenceException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password incorrect");
        }
    }

    @PostMapping("/signup")
    public @ResponseBody String signUp(@RequestBody SignUpRequest request) {
        try {
            if (request.firstname == null || request.email == null || request.password == null || request.phone == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Input");
            if (!validateEmail(request.email))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email");
            if (userRepository.findUsersByEmail(request.email).size() > 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

            Users user = new Users(
                    request.firstname,
                    request.lastname,
                    request.email,
                    bCryptPasswordEncoder.encode(request.password),
                    request.picture,
                    request.description,
                    request.phone,
                    new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            return jwtBuilder.generateToken(Integer.toString(user.getId()), "USER");
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
        }
    }

    public boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
