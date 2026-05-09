package com.interpreter;

import com.interpreterNodes.*;

import java.util.*;

/**
 * Evaluates an Abstract Syntax Tree by recursively visiting each node via evaluate().<br>
 * Runtime state (variables, functions, call stack) is managed by InterpreterMemory.
 */
public class TreeInterpreter {

    public InterpreterMemory memory = new InterpreterMemory();

    /**
     * Evaluates a single AST node and returns its result.
     * Side-effect statements return null.
     * Value-producing nodes return an Integer.
     */
     public Object evaluate(InterpreterNode root) {

        switch (root) {
            case FunctionCallNode node -> { return evaluateFunctionCall(node); }
            case FunctionNode node -> {     return evaluateFunction(node); }
            case WhileNode node -> {        return evaluateWhile(node); }
            case IfNode node -> {           return evaluateIf(node); }
            case AssignNode node -> {       return evaluateAssign(node); }
            case BinaryOpNode node -> {     return evaluateBinOp(node); }
            case ValueNode node -> {        return evaluateValue(node); }
            case VariableNode node -> {     return evaluateVariable(node); }
            default -> throw new InterpreterException("Unknown node type");
        }
    }

    /** Resolves the function, evaluates arguments, pushes a stack frame, and executes the body. */
    private Object evaluateFunctionCall(FunctionCallNode node) {

        FunctionNode functionNode = memory.getFunction(node.name);

        Map<String,Object> parametersToArguments = evaluatesArguments(functionNode,node);

        memory.pushStackFrame(parametersToArguments);

        return executeFunction(functionNode);
    }

    /** Evaluates each argument expression and maps it to its corresponding parameter name. */
    private Map<String, Object> evaluatesArguments(FunctionNode functionNode, FunctionCallNode callNode) {
        Map<String, Object> parametersToArguments = new HashMap<>();

        for (int i = 0; i < callNode.arguments.size(); i++) {
            VariableNode parameter = functionNode.parameters.get(i);
            InterpreterNode argument = callNode.arguments.get(i);

            if (parameter == null || argument == null) {
                throw new InterpreterException("Invalid amount of parameters: Expected " + functionNode.parameters.size() + "but got " + callNode.arguments.size());
            }

            parametersToArguments.put(parameter.getVariableName(), evaluate(argument));
        }

        return parametersToArguments;
    }

    /**
     * Executes the function body statement by statement.
     * Returns if a ReturnException is caught.
     * The stack frame is always popped in the 'finally' block.
     */
    private Object executeFunction(FunctionNode functionNode) {

        Integer evaluatedFunctionResult = null;

        try {
            for (InterpreterNode statement : functionNode.body) {
                Object statementResult = evaluate(statement);
                if (statementResult != null) evaluatedFunctionResult = (Integer) statementResult;
            }
        } catch (ReturnException e) {
            return e.returnValue();

        } finally {

            memory.popStackFrame();

        }
        return evaluatedFunctionResult;
    }


    /** Registers a function definition in memory. */
    private Object evaluateFunction(FunctionNode node) {
        memory.defineFunction(node);
        return null;
    }

    /** Evaluates the condition and executes the appropriate branch, returning its result. */
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

    /** Evaluates the condition and executes the appropriate branch, returning its result. */
    private Object evaluateIf(IfNode node) {
        boolean isConditionTrue = isTrue(evaluate(node.condition));

        if (isConditionTrue) {
            return evaluate(node.thenExpression);
        } else {
            return evaluate(node.elseExpression);
        }

    }

    /** Evaluates the right-hand side and stores the result in memory. */
    private Void evaluateAssign(AssignNode node) {
        memory.setVariable(node.variable.getVariableName(), evaluate(node.variableValue));
        return null;
    }

    /** Evaluates both operands and applies the binary operator. */
    private int evaluateBinOp(BinaryOpNode node) {

        switch (node.operator) {
            case Operator.MULT -> { return (Integer) evaluate(node.left) * (Integer) evaluate(node.right);}

            case Operator.DIV -> {

                Integer divisor = (Integer) evaluate(node.right);

                if (divisor == 0) {
                    throw new InterpreterException("Division by zero");
                }
                return (Integer) evaluate(node.left) / (Integer) evaluate(node.right);
            }

            case Operator.PLUS -> { return (Integer) evaluate(node.left) + (Integer) evaluate(node.right); }

            case Operator.MINUS -> { return (Integer) evaluate(node.left) - (Integer) evaluate(node.right); }

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

    /** Returns the integer value stored in the node. */
    private Integer evaluateValue(ValueNode node) {
        return node.value;
    }

    /** Looks up the variable value in memory. */
    private Object evaluateVariable(VariableNode node) {

        String variableName = node.getVariableName();

        return memory.getVariableValue(variableName);
    }

    private boolean isTrue(Object value) {
        return ((int) value) != 0;
    }
}