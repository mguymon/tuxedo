package com.tobedevoured.tuxedo.api;

/**
 */
public enum Status {
    SUCCESS("success","001"),

    // Invalid request data
    INVALID_ID("error", "501"),

    // Malformed request
    MALFORMED("error", "901");


    public final String message;
    public final String code;

    Status(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
