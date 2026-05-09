package com.interpreter;

/**
 * Thrown in certain circumstances when a return statement is reached inside a function body.<br>
 * Used as a control flow mechanism to exit nested while/if structures immediately.<br>
 * Caught by evaluateFunctionCall, which extracts the return value.
 */
public class ReturnException extends RuntimeException {
    private final Object value;

     ReturnException(Object value) {
        this.value = value;
    }

     Object returnValue() {
        return this.value;
    }
}