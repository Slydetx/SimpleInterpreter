package com.tokenizer;

/**
 * Represents a single token produced by the Tokenizer.
 * Each token has a type (what kind of token it is) and a value (the original source text).
 */
public class Token {
    public TokenType tokenType;
    public String tokenValue;

    public Token (TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;

    }

    /** Parses and returns the token value as an integer. Only valid for VAL tokens. */
    public int getNumericValue() {
        return Integer.parseInt(this.tokenValue);
    }
}
