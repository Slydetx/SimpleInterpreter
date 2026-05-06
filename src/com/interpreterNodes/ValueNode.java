package com.interpreterNodes;

public class ValueNode implements InterpreterNode {
    int value;

    public ValueNode(int value) {
        this.value = value;
    }
}
