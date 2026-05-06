package com.interpreter;

import com.interpreterNodes.*;

import java.util.HashMap;
import java.util.Map;

public class TreeInterpreter {

    public Map<String,Integer> variablesToValues = new HashMap<>();

    public Object evaluate (InterpreterNode root) {

        switch (root) {
            case AssignNode node -> {return evaluateAssign(node);}
            case BinaryOpNode node -> {return evaluateBinOp(node);}
            case ValueNode node -> {return evaluateValue(node);}
            case VariableNode node -> {return evaluateVariable(node);}
            default -> throw new IllegalArgumentException("Unknown node type");
        }
    }

    private Void evaluateAssign(AssignNode node) {
         variablesToValues.put(
                node.variable.getVariableName(),
                (int)evaluate(node.variableValue));
        return null;
    }

    private int evaluateBinOp (BinaryOpNode node) {

        switch (node.operator) {
            case Operator.MULT -> {
                return (Integer) evaluate(node.left) * (Integer) evaluate(node.right);
            }
            case Operator.PLUS -> {
                return (Integer) evaluate(node.left) + (Integer) evaluate(node.right);
            }
            default -> throw new IllegalStateException("Unknown operator");
        }
    }

    private int evaluateValue(ValueNode node) {
        return node.value;
    }

    private int evaluateVariable(VariableNode node) {
        return variablesToValues.get(node.getVariableName());
    }

    public void printMemory() {

        for (Map.Entry<String, Integer> entry : this.variablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = entry.getValue();

            System.out.println(key + ": " + value);

        }
    }
}