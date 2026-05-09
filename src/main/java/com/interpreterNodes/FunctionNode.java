package com.interpreterNodes;

import java.util.List;

/**
 * Represents a function definition.
 * Example: fun add(a, b) { return a + b }
 */
public class FunctionNode implements InterpreterNode {
    public VariableNode name;
    public List<VariableNode> parameters;
    public List<InterpreterNode> body;

}
