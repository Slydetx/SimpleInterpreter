package com.interpreterNodes;

import com.tokenizer.Token;

/**
 * Represents a variable reference.
 * Example: x, result, n
 */
public class VariableNode implements InterpreterNode {
    Token token;

    public VariableNode(Token token) {
        this.token = token;
    }

    public String getVariableName() {
        return this.token.tokenValue;
    }
}