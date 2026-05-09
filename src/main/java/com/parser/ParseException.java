package com.parser;

/**
 * Thrown when the parser encounters unexpected or invalid syntax.
 */
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}