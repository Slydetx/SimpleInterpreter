package com.interpreterNodes;

/**
 * Represents a binary operation between two expressions.
 * Covers arithmetic (+, -, *, /) and comparison (==, !=, <, <=, >, >=) operators.
 * Example: x + 2, n <= 0, a * fact_rec(n - 1)
 */
public class BinaryOpNode implements InterpreterNode {
    public InterpreterNode left;
    public InterpreterNode right;
    public Operator operator;

    public BinaryOpNode(InterpreterNode left, InterpreterNode right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}