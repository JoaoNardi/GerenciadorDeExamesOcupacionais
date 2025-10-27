package com.joaonardi.gerenciadorocupacional.exception;

public class DataDuplicityException extends RuntimeException {
    public DataDuplicityException(String message) {
        super(message);
    }
    public DataDuplicityException(String message, Throwable cause) {
        super(message, cause);
    }
}
