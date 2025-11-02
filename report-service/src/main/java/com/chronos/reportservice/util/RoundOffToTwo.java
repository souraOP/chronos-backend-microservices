package com.chronos.reportservice.util;

public class RoundOffToTwo {
    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
