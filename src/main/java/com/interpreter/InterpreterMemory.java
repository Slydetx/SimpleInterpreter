package com.interpreter;

import com.interpreterNodes.FunctionNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class InterpreterMemory {
    public Stack<Map<String, Object>> callStack = new Stack<>();
    public Map<String, Object> globalVariablesToValues = new HashMap<>();
    public Map<String, FunctionNode> variablesToFunctions = new HashMap<>();


    public void printMemory() {

        for (Map.Entry<String, Object> entry : this.globalVariablesToValues.entrySet()) {

            String key = entry.getKey();
            Integer value = (Integer) entry.getValue();

            System.out.println(key + ": " + value);

        }
    }

    void popStackFrame() {
        this.callStack.pop();
    }

    private Map<String, Object> getCurrentFrame() {

        if (callStack.isEmpty()) return null;

        return callStack.peek();
    }

    FunctionNode getFunction(String name) {
        FunctionNode function = variablesToFunctions.get(name);
        if (function == null) {
            throw new InterpreterException("Undefined function: " + name);
        }
        return function;
    }

    public void pushStackFrame(Map<String, Object> parametersToArguments) {
        callStack.add(new HashMap<>());
        getCurrentFrame().putAll(parametersToArguments);
    }

    public void defineFunction(String variableName, FunctionNode node) {
        variablesToFunctions.put(
                node.name.getVariableName(),
                node);
    }

    public void setVariable(String variableName, Object value) {
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

    public Integer getVariableValue(String variableName) {

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
