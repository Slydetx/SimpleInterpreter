package com.interpreterNodes;

import java.util.List;

/**
 * Represents a while loop.
 * The body is a list of comma-separated statements executed on each iteration.
 * Example: while x < 3 do y = y + 1, x = x + 1
 */
public class WhileNode implements InterpreterNode {
    public InterpreterNode condition;
    public List<InterpreterNode> body;
}