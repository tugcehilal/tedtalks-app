package com.tugce.tedtalksapp.tedtalks.exception;

/**
 * A custom exception for handling CSV parsing errors.
 */
public class CsvParseException extends RuntimeException {

    public CsvParseException(String message) {
        super(message);
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
