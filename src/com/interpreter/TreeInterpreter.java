package com.interpreter;

import com.interpreterNodes.*;

import java.util.HashMap;
import java.util.Map;

public class TreeInterpreter {

    public Map<String,Integer> variablesToValues = new HashMap<>();
    public Map<String,FunctionNode> variablesToFunctions = new HashMap<>();

    public Object evaluate (InterpreterNode root) {

        switch (root) {
            case FunctionNode node    -> {return evaluateFunction(node);}
            case WhileNode node    -> {return evaluateWhile(node);}
            case IfNode node       -> {return evaluateIf(node);}
            case AssignNode node   -> {return evaluateAssign(node);}
            case BinaryOpNode node -> {return evaluateBinOp(node);}
            case ValueNode node    -> {return evaluateValue(node);}
            case VariableNode node -> {return evaluateVariable(node);}
            default -> throw new IllegalArgumentException("Unknown node type");
        }
    }

    private Object evaluateFunction(FunctionNode node) {
        variablesToFunctions.put(
                node.name.getVariableName(),
                node);
        return null;
    }

    private Void evaluateWhile (WhileNode node) {

        while (isTrue(evaluate(node.condition))) {
            for (InterpreterNode statement : node.body) {
                evaluate(statement);
            }
        }
        return null;
    }

    private Void evaluateIf(IfNode node) {
        boolean isConditionTrue = isTrue(evaluate(node.condition));

        if (isConditionTrue) {
            evaluate(node.thenExpression);
        } else {
            evaluate(node.elseExpression);
        }
        return null;
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

            case Operator.GT -> {
                boolean greaterThan = (Integer) evaluate(node.left) > (Integer) evaluate(node.right);
                return greaterThan ? 1 : 0;
            }

            case Operator.LT -> {
                boolean lessThan = (Integer) evaluate(node.left) < (Integer) evaluate(node.right);
                return lessThan ? 1 : 0;
            }

            case Operator.EQ -> {
                boolean equal = (Integer) evaluate(node.left) == (Integer) evaluate(node.right);
                return equal ? 1 : 0;
            }

            default -> throw new IllegalStateException("Unknown operator");
        }
    }

    private int evaluateValue(ValueNode node) {
        return node.value;
    }

    private Object evaluateVariable(VariableNode node) {
         Integer variableValue = variablesToValues.get(node.getVariableName());

         if (variableValue == null) {
             FunctionNode functionNode = variablesToFunctions.get(node.getVariableName());
             throw new IllegalArgumentException();

         } else {
             return variableValue;
         }

    }

    public void printMemory() {

        for (Map.Entry<String, Integer> entry : this.variablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = entry.getValue();

            System.out.println(key + ": " + value);

        }
    }

    public boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}