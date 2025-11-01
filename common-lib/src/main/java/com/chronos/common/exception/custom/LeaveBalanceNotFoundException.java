package com.chronos.common.exception.custom;

public class LeaveBalanceNotFoundException extends RuntimeException {
    public LeaveBalanceNotFoundException(String message) {
        super(message);
    }
}
