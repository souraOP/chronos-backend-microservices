package com.chronos.employeeservice.util;


import java.util.UUID;

public class ParseUUID {
    public static UUID parseUUID(String s, String message){
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(message);
        }
    }
}