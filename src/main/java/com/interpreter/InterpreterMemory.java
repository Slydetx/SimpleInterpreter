package com.interpreter;

import com.interpreterNodes.FunctionNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Manages the runtime memory of the interpreter.<br>
 * Holds three distinct data structures:
 * <ul>
 *   <li>A global variable map for top-level assignments</li>
 *   <li>A call stack of stack frames for function-local variables</li>
 *   <li>A function definition table</li>
 * </ul>
 *
 * Variable lookup checks the current stack frame first, then falls back to globals.
 * Stack frames are pushed on function entry and popped on exit (always via finally).
 */
public class InterpreterMemory {
    public Stack<Map<String, Object>> callStack = new Stack<>();
    public Map<String, Object> globalVariablesToValues = new HashMap<>();
    public Map<String, FunctionNode> variablesToFunctions = new HashMap<>();

    /** Prints all global variables to stdout in "key: value" format. */
    public void printMemory() {

        for (Map.Entry<String, Object> entry : this.globalVariablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = (Integer) entry.getValue();

            System.out.println(key + ": " + value);

        }
    }
    /**
     * Pushes a new stack frame with the given parameters onto the call stack. */
    void popStackFrame() {
        this.callStack.pop();
    }

    /** Returns the current stack frame or null if the call stack is empty. */
    private Map<String, Object> getCurrentFrame() {

        if (callStack.isEmpty()) return null;

        return callStack.peek();
    }

    /**
     * Looks up a function by name.
     * @throws InterpreterException if the function is not defined.
     */
    FunctionNode getFunction(String name) {
        FunctionNode function = variablesToFunctions.get(name);
        if (function == null) {
            throw new InterpreterException("Undefined function: " + name);
        }
        return function;
    }

    /** Stores a function definition in the function table. */
    void pushStackFrame(Map<String, Object> parametersToArguments) {
        callStack.push(new HashMap<>(parametersToArguments));
    }

    /** Stores a function definition in the function table. */
    void defineFunction( FunctionNode node) {
        variablesToFunctions.put(
                node.name.getVariableName(),
                node);
    }

    /**
     * Assigns a value to a variable.
     * Writes to the current stack frame if inside a function, otherwise to globals.
     */
    void setVariable(String variableName, Object value) {
        if (getCurrentFrame() == null) {
            globalVariablesToValues.put(
                    variableName,
                    value);
        } else {

            getCurrentFrame().put(
                    variableName,
                    value);
        }
    }

    /**
     * Retrieves the value of a variable.
     * Checks the current stack frame first, then falls back to globals.
     * @throws InterpreterException if the variable is not defined.
     */
    Integer getVariableValue(String variableName) {

        Integer variableValue = null;

        if (getCurrentFrame() != null)
            variableValue = (Integer) getCurrentFrame().get(variableName);

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
}
