package com.buyersfirst.auth.controllers;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.buyersfirst.auth.interfaces.OTP;
import com.buyersfirst.auth.interfaces.PasswordRest;
import com.buyersfirst.auth.interfaces.SignUpRequest;
import com.buyersfirst.auth.interfaces.TokenRequest;
import com.buyersfirst.auth.models.UserRepository;
import com.buyersfirst.auth.models.Users;
import com.buyersfirst.auth.services.Helpers;
import com.buyersfirst.auth.services.JWTBuilder;
import com.buyersfirst.auth.services.NotificationService;
import com.buyersfirst.auth.services.RedisCacheService;

import jakarta.persistence.PersistenceException;

@CrossOrigin
@Controller
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTBuilder jwtBuilder;
    @Autowired
    NotificationService notificationService;
    @Autowired
    Helpers helperMethods;
    @Autowired
    RedisCacheService redisCacheService;

    @Value("${template.otp}")
    private String otpTemplate;
    @Value("${template.resetotp}")
    private String otpResetTemplate;

    @PostMapping("")
    public @ResponseBody String getToken(@RequestBody TokenRequest auth) {
        try {
            Users user = userRepository.findByEmail(auth.email);
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
            if (bCryptPasswordEncoder.matches(auth.password, user.getPassword())) {
                return jwtBuilder.generateToken(user.getUuid().toString(), "USER");
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
            if (request.first_name == null || request.email == null || request.password == null || request.phone == null
                    || request.otp == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Input");
            if (!helperMethods.validateEmail(request.email))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email");
            if (userRepository.findUsersByEmail(request.email).size() > 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken");

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

            // Check if the otp supplied hashed and email match
            if (!bCryptPasswordEncoder.matches(request.otp,
                    redisCacheService.jedis.get(request.email)))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");

            // if match remove from redis and proceed to to DB
            Users user = new Users(
                    request.first_name,
                    request.last_name,
                    request.email,
                    bCryptPasswordEncoder.encode(request.password),
                    request.picture,
                    request.description,
                    request.phone,
                    new Timestamp(System.currentTimeMillis()),
                    request.affiliateCode);
            userRepository.save(user);
            // Remove OTP from cache
            redisCacheService.jedis.del(request.email);
            // Return a jwt token
            return jwtBuilder.generateToken(user.getUuid().toString(), "USER");
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
        }
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public void sendOTP(@RequestBody OTP bodyOtp) {
        try {
            // Generate OTP
            String OTP[] = { Integer.toString(helperMethods.generateRandomNumber()) };
            String msgToBeSent = bodyOtp.ForReset ? helperMethods.insertStrings(otpResetTemplate, OTP)
                    : helperMethods.insertStrings(otpTemplate, OTP);
            System.out.println(msgToBeSent);
            if (!helperMethods.validateEmail(bodyOtp.email) && bodyOtp.email != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email");
            }
            if (!helperMethods.validatePhone(bodyOtp.phone) && bodyOtp.phone != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Phone");
            }
            if (bodyOtp.ForReset && userRepository.findUsersByEmail(bodyOtp.email.get()).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email doesn't exist");
            }
            if (helperMethods.validateEmail(bodyOtp.email)) {
                // Email
                if (bodyOtp.ForReset) {
                    if (!notificationService.sendEmail(bodyOtp.email.get(), "Password Reset Code", msgToBeSent))
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't send to queue");
                } else {
                    if (!notificationService.sendEmail(bodyOtp.email.get(), "One Time Passcode", msgToBeSent))
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't send to queue");
                }
            }
            if (helperMethods.validatePhone(bodyOtp.phone)) {
                // Phone
                if (!notificationService.sendSMS(bodyOtp.phone.get(), msgToBeSent))
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't send to queue");
            }

            // Hash the OTP
            BCryptPasswordEncoder hasher = new BCryptPasswordEncoder(10);
            String hashedOTP = hasher.encode(OTP[0]);

            // Send it to Redis with email as key
            redisCacheService.jedis.setex(bodyOtp.email.get(), 300, hashedOTP);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public void resetPassword(@RequestBody PasswordRest request) {
        try {
            if (request.newPassword == null || request.otp == null || request.email == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Input");

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

            // Check if the otp supplied hashed and email match
            if (!bCryptPasswordEncoder.matches(request.otp,
                    redisCacheService.jedis.get(request.email)))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");

            // Update User Password
            userRepository.updateUserPass(request.email, bCryptPasswordEncoder.encode(request.newPassword));

            // Remove OTP from cache
            redisCacheService.jedis.del(request.email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}
