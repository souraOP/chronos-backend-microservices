package com.chronos.common.exception.custom;

public class ShiftNotFoundException extends RuntimeException {
    public ShiftNotFoundException(String message) {
        super(message);
    }
}
