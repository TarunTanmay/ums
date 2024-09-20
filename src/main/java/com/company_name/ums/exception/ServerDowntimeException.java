package com.company_name.ums.exception;

public class ServerDowntimeException extends RuntimeException {
    public ServerDowntimeException(String message) {
        super(message);
    }
}
