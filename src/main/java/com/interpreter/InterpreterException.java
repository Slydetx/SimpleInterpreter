package com.interpreter;

/**
 * Thrown when a runtime error occurs during interpretation,
 * such as an undefined variable, undefined function, or division by zero.
 */
public class InterpreterException extends RuntimeException {
    public InterpreterException(String message) {
        super(message);
    }
}