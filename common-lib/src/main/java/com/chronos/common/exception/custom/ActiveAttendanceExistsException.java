package com.chronos.common.exception.custom;

public class ActiveAttendanceExistsException extends RuntimeException {
    public ActiveAttendanceExistsException(String message) {
        super(message);
    }
}
