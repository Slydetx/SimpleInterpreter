package com.interpreter;

import com.interpreterNodes.*;

import java.util.*;

public class TreeInterpreter {

    public Stack<Map<String,Object>> callStack = new Stack<>();
    public Map<String,Object> globalVariablesToValues = new HashMap<>();
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

        Map<String, Object> parametersToArguments = new HashMap<>();

        for (int i = 0; i < node.arguments.size(); i++ ) {
            VariableNode parameter = functionNode.parameters.get(i);
            InterpreterNode argument = node.arguments.get(i);

            if (parameter == null || argument == null ) {
                throw new InterpreterException("Invalid amount of parameters: Expected " + functionNode.parameters.size() + "but got " + node.arguments.size());
            }

            parametersToArguments.put(parameter.getVariableName(), evaluate(argument));
        }
        callStack.add(new HashMap<>());
        getCurrentScope().putAll(parametersToArguments);

        Integer evaluatedFunctionResult = (Integer) evaluate(functionNode.returnBody);

        removeFunctionCallFromStackFrame(functionNode);

        return evaluatedFunctionResult;

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

    private Object evaluateIf(IfNode node) {
        boolean isConditionTrue = isTrue(evaluate(node.condition));

        if (isConditionTrue) {
            return evaluate(node.thenExpression);
        } else {
            return evaluate(node.elseExpression);
        }
    }

    private Void evaluateAssign(AssignNode node) {
         globalVariablesToValues.put(
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

            case Operator.MINUS -> {
                boolean equal = evaluate(node.left) == evaluate(node.right);
                return (Integer) evaluate(node.left) - (Integer) evaluate(node.right);
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

            case Operator.LT_EQ -> {
                boolean equal = (Integer) evaluate(node.left) <= (Integer) evaluate(node.right);
                return equal ? 1 : 0;
            }

            default -> throw new InterpreterException("Unknown operator");
        }
    }

    private int evaluateValue(ValueNode node) {
        return node.value;
    }

    private Object evaluateVariable(VariableNode node) {

        String variableName = node.getVariableName();

        Integer variableValue = (Integer) getCurrentScope().get(variableName);

        if (variableValue != null) {
            return variableValue;
        }

        variableValue = (Integer) globalVariablesToValues.get(variableName);

        if (variableValue != null) {
            return variableValue;
        }

        throw new InterpreterException(
                "Invalid variable: " + variableName
        );
    }

    public void printMemory() {

        for (Map.Entry<String, Object> entry : this.globalVariablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = (Integer) entry.getValue();

            System.out.println(key + ": " + value);

        }
    }

    private void removeFunctionCallFromStackFrame(FunctionNode functionNode) {
        this.callStack.pop();
    }

    private Map<String,Object> getCurrentScope() {
        return callStack.peek();
    }

    public boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}