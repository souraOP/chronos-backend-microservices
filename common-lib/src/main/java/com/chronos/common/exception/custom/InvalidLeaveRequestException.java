package com.chronos.common.exception.custom;

public class InvalidLeaveRequestException extends RuntimeException {
    public InvalidLeaveRequestException(String message) {
        super(message);
    }
}
