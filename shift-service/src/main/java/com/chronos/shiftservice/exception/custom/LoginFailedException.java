package com.chronos.shiftservice.exception.custom;

public class LoginFailedException extends RuntimeException {
    // this exception is given for
    public LoginFailedException(String message) {
        super(message);
    }
}
