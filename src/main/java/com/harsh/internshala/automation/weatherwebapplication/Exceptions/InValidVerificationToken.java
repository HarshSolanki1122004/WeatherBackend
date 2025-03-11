package com.harsh.internshala.automation.weatherwebapplication.Exceptions;

public class InValidVerificationToken extends RuntimeException {
    public InValidVerificationToken(String message) {
        super(message);
    }
}
