package com.interpreter;

public class ReturnException extends RuntimeException {
    private Object value;

    public ReturnException (Object value) {
        this.value = value;
    }

    public Object returnValue() {
        return this.value;
    }
}
