package com.interpreterNodes;

/**
 * Represents an assignment statement: variable = expression
 * Example: x = (a + b) * 2
 */
public class AssignNode implements InterpreterNode {
    public VariableNode variable;
    public InterpreterNode variableValue;
}