package com.chronos.shiftservice.exception.custom;

public class ActiveAttendanceNotFoundException extends RuntimeException {
    public ActiveAttendanceNotFoundException(String message) {
        super(message);
    }
}
