package com.interpreterNodes;

import java.util.List;

/**
 * Represents a function call expression.
 * Example: add(2, 2), fact_rec(n - 1)
 */
public class FunctionCallNode implements InterpreterNode {
    public String name;
    public List<InterpreterNode> arguments;

}
