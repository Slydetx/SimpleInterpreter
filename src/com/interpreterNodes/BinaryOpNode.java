package com.interpreterNodes;

public class BinaryOpNode implements InterpreterNode {
    public InterpreterNode left;
    public InterpreterNode right;
    public Operator operator;

    public BinaryOpNode(){}
    public BinaryOpNode (InterpreterNode left, InterpreterNode right, Operator operator){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

}
