package com.tokenizer;

import java.util.List;
import java.util.Map;

public class TokenClassifier {

    List<Token> tokenList;

    private static final Map<String, TokenType> OPERATORS = Map.of(
            "=", TokenType.ASSIGN,
            "+", TokenType.PLUS,
            "-", TokenType.MINUS,
            "*", TokenType.MULT,
            ">", TokenType.GT,
            "<", TokenType.LT,
            "==", TokenType.EQ,
            "<=", TokenType.LT_EQ
    );

    private static final Map<String, TokenType> PUNCTUATION = Map.of(
            "(", TokenType.LPAR,
            ")", TokenType.RPAR,
            ",", TokenType.COMMA,
            "{", TokenType.LBRACE,
            "}", TokenType.RBRACE
    );

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "if", TokenType.IF,
            "then", TokenType.THEN,
            "else", TokenType.ELSE,
            "while", TokenType.WHILE,
            "do", TokenType.DO,
            "fun", TokenType.FUN,
            "return", TokenType.RETURN,
            "true", TokenType.TRUE,
            "false", TokenType.FALSE
    );

    TokenClassifier(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    void mapToken(String rawToken) {

        //check if it's an operator
        TokenType tokenType = OPERATORS.get(rawToken);

        //check if it's a keyword
        if (isTokenNotFound(tokenType)) {
            tokenType = KEYWORDS.get(rawToken);
        }

        //check if it's punctuation
        if (isTokenNotFound(tokenType)) {
            tokenType = PUNCTUATION.get(rawToken);
        }

        //if still not found, then it's either a value or a variable
        if (isTokenNotFound(tokenType)) {
            tokenType = findDynamicTokenType(rawToken);
        }

        Token token = new Token(tokenType, rawToken);
        this.tokenList.add(token);
    }


    private boolean isTokenNotFound(TokenType tokenType) {
        return tokenType == null;
    }

    private TokenType findDynamicTokenType (String word) {
        boolean wordIsADigit = true;

        for (char character : word.toCharArray()) {
            wordIsADigit &= Character.isDigit(character);
        }

        return wordIsADigit ? TokenType.VAL : TokenType.VAR;
    }
}
