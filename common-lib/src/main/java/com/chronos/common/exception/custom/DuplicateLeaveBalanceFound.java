package com.chronos.common.exception.custom;

public class DuplicateLeaveBalanceFound extends RuntimeException {
    public DuplicateLeaveBalanceFound(String message) {
        super(message);
    }
}
