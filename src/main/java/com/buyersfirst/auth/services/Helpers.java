package com.buyersfirst.auth.services;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class Helpers {
    public boolean validateEmail(Optional<String> email) {
        if (email == null)
            return false;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.get());
        return matcher.matches();
    }

    public boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePhone(Optional<String> phone) {
        if (phone == null)
            return false;
        String regex = "^\\+251[79][0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone.get());
        return matcher.matches();
    }

    public int generateRandomNumber() {
        // Create an instance of the Random class
        Random random = new Random();
        // Generate a random 6-digit number
        int randomNumber = 100000 + random.nextInt(900000);
        System.out.println(randomNumber);
        return randomNumber;
    }

    public String insertStrings(String target, String[] insertions) {
        StringBuilder result = new StringBuilder(target);
        int insertionIndex = 0;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '*') {
                // Replace '*' with the next element from insertions array
                if (insertionIndex < insertions.length) {
                    result.replace(i, i + 1, insertions[insertionIndex]);
                    insertionIndex++;
                } else {
                    // If insertions array is exhausted, break the loop
                    break;
                }
            }
        }
        return result.toString();
    }
}
