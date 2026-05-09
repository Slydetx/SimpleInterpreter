package com.interpreterNodes;

/**
 * Represents an integer literal or boolean literal (true = 1, false = 0).
 * Example: 42, 0, true, false
 */
public class ValueNode implements InterpreterNode {
    public int value;

    public ValueNode(int value) {
        this.value = value;
    }
}