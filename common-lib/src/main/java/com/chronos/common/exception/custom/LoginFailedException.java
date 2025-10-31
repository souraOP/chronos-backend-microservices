package com.chronos.common.exception.custom;

public class LoginFailedException extends RuntimeException {
    // this exception is given for
    public LoginFailedException(String message) {
        super(message);
    }
}
