package com.interpreterNodes;

/**
 * Represents an if/else statement.
 * Both branches are mandatory: the language does not support if without else.
 * Example: if x > 10 then y = 100 else y = 0
 */
public class IfNode implements InterpreterNode {
    public InterpreterNode condition;
    public InterpreterNode thenExpression;
    public InterpreterNode elseExpression;
}
