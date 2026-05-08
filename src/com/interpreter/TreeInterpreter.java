package com.interpreter;

import com.interpreterNodes.*;

import java.util.HashMap;
import java.util.Map;

public class TreeInterpreter {

    public Map<String,Integer> variablesToValues = new HashMap<>();
    public Map<String,FunctionNode> variablesToFunctions = new HashMap<>();

    public Object evaluate (InterpreterNode root) {

        switch (root) {
            case FunctionCallNode node -> {return evaluateFunctionCall(node);}
            case FunctionNode node     -> {return evaluateFunction(node);}
            case WhileNode node        -> {return evaluateWhile(node);}
            case IfNode node           -> {return evaluateIf(node);}
            case AssignNode node       -> {return evaluateAssign(node);}
            case BinaryOpNode node     -> {return evaluateBinOp(node);}
            case ValueNode node        -> {return evaluateValue(node);}
            case VariableNode node     -> {return evaluateVariable(node);}
            default -> throw new InterpreterException ("Unknown node type");
        }
    }

    private Object evaluateFunctionCall(FunctionCallNode node) {

        FunctionNode functionNode = variablesToFunctions.get(node.name);

        for (int i = 0; i < node.arguments.size(); i++ ) {
            VariableNode parameter = functionNode.parameters.get(i);
            ValueNode argument = (ValueNode) node.arguments.get(i);

            if (parameter == null || argument == null ) {
                throw new InterpreterException("Invalid Amount of parameters: Expected " + functionNode.parameters.size() + "but got " + node.arguments.size());
            }

            variablesToValues.put(parameter.getVariableName(), argument.value);
        }

        Integer evaluatedFunctionResult = (Integer) evaluate(functionNode.returnBody);

        removeLocalFunctionVariables(functionNode);

        return evaluatedFunctionResult ;

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
                boolean equal = evaluate(node.left) == evaluate(node.right);
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

             throw new InterpreterException("Invalid variable: " + node.getVariableName());

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

    public void removeLocalFunctionVariables(FunctionNode functionNode) {
        for (VariableNode parameter : functionNode.parameters)
            variablesToValues.remove(parameter.getVariableName());
    }

    public boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}