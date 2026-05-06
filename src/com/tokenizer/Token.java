package com.tokenizer;

public class Token {
    public TokenType tokenType;
    public String tokenValue;

    public Token (TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;

    }
}
