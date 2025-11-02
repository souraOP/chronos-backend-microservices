package com.chronos.reportservice.util;

import java.security.SecureRandom;

public class ReportIdGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateReportId() {

        int n = 1000 + random.nextInt(1000);
        return "RPT-" + n;
    }
}
