package com.chronos.shiftservice.utils;


import com.chronos.shiftservice.exception.custom.InvalidUUIDException;

import java.util.UUID;

public class ParseUUID {
    public static UUID parseUUID(String s, String message){
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException(message);
        }
    }
}