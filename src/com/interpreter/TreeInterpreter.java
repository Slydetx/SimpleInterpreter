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


        Integer evaluatedFunctionResult = null;

        try {
            for (InterpreterNode statement : functionNode.body) {
                evaluatedFunctionResult = (Integer) evaluate(statement);
            }
        } catch (ReturnException e) {

            return e.returnValue();
        }
         finally {
            removeFunctionCallFromStackFrame(functionNode);
        }


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
                // The result of evaluate(statement) is intentionally ignored for normal statements.
                // However, when a 'return' occurs inside a nested while/if, the return value has
                // nowhere to go: the while loop keeps running (debugger confirmed: r=120, n=0
                // are correct, but execution continues into n=-1).
                // ReturnException solves this by bubbling up through evaluateWhile/evaluateIf
                // back to evaluateFunctionCall, which catches it and stops execution.
                evaluate(statement);
            }
        }
        return null;
    }

    private Object evaluateIf(IfNode node) {
        boolean isConditionTrue = isTrue(evaluate(node.condition));

        Object evaluatedExpression;

        if (isConditionTrue) {
            evaluatedExpression = evaluate(node.thenExpression);
        } else {
            evaluatedExpression = evaluate(node.elseExpression);
        }

        // All side-effect statements return null
        // If this assumption ever breaks, implementation should be changed
        if (evaluatedExpression == null) {
            return null;
        } else {
            // Non-null means a 'return' occurred (only return produces a value)
            throw new ReturnException(evaluatedExpression);
        }
    }

    private Void evaluateAssign(AssignNode node) {

        if (getCurrentScope() == null) {
            globalVariablesToValues.put(
                    node.variable.getVariableName(),
                    (int)evaluate(node.variableValue));
        } else {

            getCurrentScope().put(
                    node.variable.getVariableName(),
                    (int)evaluate(node.variableValue));
        }
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
        Integer variableValue = null;

        if (getCurrentScope() != null)
            variableValue = (Integer) getCurrentScope().get(variableName);

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

        if (callStack.isEmpty()) return null;

        return callStack.peek();
    }

    public boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}