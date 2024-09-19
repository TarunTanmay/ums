package org.example.ums.exception;

import lombok.*;

@Setter @Getter
public class ErrorResponse {
    private int statusCode;
    private String message;
    private long timestamp;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
