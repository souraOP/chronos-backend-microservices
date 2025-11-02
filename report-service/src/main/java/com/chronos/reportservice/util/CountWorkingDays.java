package com.chronos.reportservice.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CountWorkingDays {
    public static int countWorkingDays(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate d = startDate;
        while (!d.isAfter(endDate)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                count++;
            }
            d = d.plusDays(1);
        }
        return count;
    }
}
