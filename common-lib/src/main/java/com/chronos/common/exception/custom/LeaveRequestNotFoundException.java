package com.chronos.common.exception.custom;

public class LeaveRequestNotFoundException extends RuntimeException {
    public LeaveRequestNotFoundException(String message) {
        super(message);
    }
}
