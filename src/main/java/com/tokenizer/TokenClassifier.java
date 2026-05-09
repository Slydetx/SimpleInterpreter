package com.tokenizer;

import java.util.List;
import java.util.Map;

/**
 * Classifies raw token strings into typed Token objects and adds them to the token list.
 */
class TokenClassifier {

    List<Token> tokenList;

    private static final Map<String, TokenType> OPERATORS = Map.ofEntries(
            Map.entry("=",  TokenType.ASSIGN),
            Map.entry("+",  TokenType.PLUS),
            Map.entry("-",  TokenType.MINUS),
            Map.entry("*",  TokenType.MULT),
            Map.entry("/",  TokenType.DIV),
            Map.entry(">",  TokenType.GT),
            Map.entry("<",  TokenType.LT),
            Map.entry("==", TokenType.EQ),
            Map.entry("<=", TokenType.LT_EQ),
            Map.entry(">=", TokenType.GT_EQ),
            Map.entry("!=", TokenType.NOT_EQ)
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

    /**
     * Classifies a raw token string and appends the resulting Token to the token list. <br>
     * Classification falls through operator → keyword → punctuation → dynamic.
     */
    void mapToken(String rawToken) {

        TokenType tokenType = OPERATORS.get(rawToken);

        if (isTokenNotFound(tokenType)) {
            tokenType = KEYWORDS.get(rawToken);
        }

        if (isTokenNotFound(tokenType)) {
            tokenType = PUNCTUATION.get(rawToken);
        }

        if (isTokenNotFound(tokenType)) {
            tokenType = findDynamicTokenType(rawToken);
        }

        Token token = new Token(tokenType, rawToken);
        this.tokenList.add(token);
    }


    private boolean isTokenNotFound(TokenType tokenType) {
        return tokenType == null;
    }

    /**
     * Determines whether an unrecognized token is a numeric literal (VAL) or identifier (VAR).
     * A token is VAL if every character is a digit, otherwise VAR.
     */
    private TokenType findDynamicTokenType (String word) {
        boolean wordIsADigit = true;

        for (char character : word.toCharArray()) {
            wordIsADigit &= Character.isDigit(character);
        }

        return wordIsADigit ? TokenType.VAL : TokenType.VAR;
    }
}
