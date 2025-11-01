package com.chronos.common.exception.custom;

public class PasswordDoNotMatchException extends RuntimeException {
    public PasswordDoNotMatchException(String message) {
        super(message);
    }
}
