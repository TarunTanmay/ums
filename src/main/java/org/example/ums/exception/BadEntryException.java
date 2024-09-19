package org.example.ums.exception;

public class BadEntryException extends RuntimeException {
    public BadEntryException(String message) {
        super(message);
    }
}
