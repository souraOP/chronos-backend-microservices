package com.chronos.leaveservice.util;


import com.chronos.leaveservice.entity.LeaveRequest;

import java.time.temporal.ChronoUnit;

public class CalculateLeaveRequestDays {
    public static int getLeaveRequestDays(LeaveRequest lr) {
        return (int) ChronoUnit.DAYS.between(
                lr.getStartDate(),
                lr.getEndDate()
        ) + 1;
    }
}