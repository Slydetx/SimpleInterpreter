package com.interpreterNodes;

public class IfNode implements InterpreterNode {
    public InterpreterNode condition;
    public InterpreterNode thenExpression;
    public InterpreterNode elseExpression;
}
