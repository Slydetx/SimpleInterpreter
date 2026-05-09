package com.interpreter;

import com.interpreterNodes.*;

import java.util.*;

public class TreeInterpreter {

    public Stack<Map<String, Object>> callStack = new Stack<>();
    public Map<String, Object> globalVariablesToValues = new HashMap<>();
    public Map<String, FunctionNode> variablesToFunctions = new HashMap<>();

    public Object evaluate(InterpreterNode root) {

        switch (root) {
            case FunctionCallNode node -> {
                return evaluateFunctionCall(node);
            }
            case FunctionNode node -> {
                return evaluateFunction(node);
            }
            case WhileNode node -> {
                return evaluateWhile(node);
            }
            case IfNode node -> {
                return evaluateIf(node);
            }
            case AssignNode node -> {
                return evaluateAssign(node);
            }
            case BinaryOpNode node -> {
                return evaluateBinOp(node);
            }
            case ValueNode node -> {
                return evaluateValue(node);
            }
            case VariableNode node -> {
                return evaluateVariable(node);
            }
            default -> throw new InterpreterException("Unknown node type");
        }
    }

    private Object evaluateFunctionCall(FunctionCallNode node) {

        FunctionNode functionNode = variablesToFunctions.get(node.name);

        Map<String, Object> parametersToArguments = new HashMap<>();

        for (int i = 0; i < node.arguments.size(); i++) {
            VariableNode parameter = functionNode.parameters.get(i);
            InterpreterNode argument = node.arguments.get(i);

            if (parameter == null || argument == null) {
                throw new InterpreterException("Invalid amount of parameters: Expected " + functionNode.parameters.size() + "but got " + node.arguments.size());
            }

            parametersToArguments.put(parameter.getVariableName(), evaluate(argument));
        }
        callStack.add(new HashMap<>());
        getCurrentScope().putAll(parametersToArguments);

        Integer evaluatedFunctionResult = null;

        try {
            for (InterpreterNode statement : functionNode.body) {
                Object statementResult = evaluate(statement);
                if (statementResult != null) evaluatedFunctionResult = (Integer) statementResult;
            }
        } catch (ReturnException e) {
            return e.returnValue();

        } finally {
            removeFunctionCallFromStackFrame();

        }
        return evaluatedFunctionResult;
    }


    private Object evaluateFunction(FunctionNode node) {
        variablesToFunctions.put(
                node.name.getVariableName(),
                node);
        return null;
    }

    private Void evaluateWhile(WhileNode node) {

        while (isTrue(evaluate(node.condition))) {
            for (InterpreterNode statement : node.body) {
                // The result of evaluate(statement) is intentionally ignored for normal statements.
                // However, when a 'return' occurs inside the while body, the return value has
                // nowhere to go: the while loop keeps running (debugger confirmed: values are
                // correct at the point of return, but execution continues into the next iteration).
                // A non-null result reliably signals a 'return' since bare expressions as statements
                // are not supported in the language.
                //  ReturnException is thrown and propagates to evaluateFunctionCall which catches it

                Object evaluatedStatement = evaluate(statement);

                if (evaluatedStatement != null) {
                    throw new ReturnException(evaluatedStatement);
                }
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

        if (getCurrentScope() == null) {
            globalVariablesToValues.put(
                    node.variable.getVariableName(),
                    evaluate(node.variableValue));
        } else {

            getCurrentScope().put(
                    node.variable.getVariableName(),
                    evaluate(node.variableValue));
        }
        return null;

    }

    private int evaluateBinOp(BinaryOpNode node) {

        switch (node.operator) {
            case Operator.MULT -> {
                return (Integer) evaluate(node.left) * (Integer) evaluate(node.right);
            }

            case Operator.DIV -> {

                Integer divisor = (Integer) evaluate(node.right);

                if (divisor == 0) {
                    throw new InterpreterException("Division by zero");
                }
                return (Integer) evaluate(node.left) / (Integer) evaluate(node.right);
            }

            case Operator.PLUS -> {
                return (Integer) evaluate(node.left) + (Integer) evaluate(node.right);
            }

            case Operator.MINUS -> {
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
                boolean equal = Objects.equals(evaluate(node.left), evaluate(node.right));
                return equal ? 1 : 0;
            }

            case Operator.LT_EQ -> {
                boolean lessThanEqual = (Integer) evaluate(node.left) <= (Integer) evaluate(node.right);
                return lessThanEqual ? 1 : 0;
            }

            case Operator.GT_EQ -> {
                boolean lessThanEqual = (Integer) evaluate(node.left) >= (Integer) evaluate(node.right);
                return lessThanEqual ? 1 : 0;
            }

            case Operator.NOT_EQ -> {
                boolean notEqual = !Objects.equals(evaluate(node.left), evaluate(node.right));
                return notEqual ? 1 : 0;
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

    public Map<String, Object> getVariables() {
        return globalVariablesToValues;
    }

    public void printMemory() {

        for (Map.Entry<String, Object> entry : this.globalVariablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = (Integer) entry.getValue();

            System.out.println(key + ": " + value);

        }
    }

    private void removeFunctionCallFromStackFrame() {
        this.callStack.pop();
    }

    private Map<String, Object> getCurrentScope() {

        if (callStack.isEmpty()) return null;

        return callStack.peek();
    }

    public boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}