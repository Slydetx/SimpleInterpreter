package com.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Character;

public class Tokenizer {

    private static final String AFTER_LPAR_BEFORE_ALPHANUM = "(?<=\\()(?=[\\w(])";
    private static final String AFTER_ALPHANUM_BEFORE_RPAR = "(?<=[\\w)])(?=\\))";
    private static final String AFTER_ALPHANUM_BEFORE_COMMA = "(?<=[\\w)])(?=,)";
    private static final String AFTER_ALPHANUM_BEFORE_LPAR = "(?<=[\\w)])(?=\\()";

    private static final String SPLIT_PATTERN = new RegexBuilder()
            .splitAt(" ")
            .or(AFTER_LPAR_BEFORE_ALPHANUM)
            .or(AFTER_ALPHANUM_BEFORE_RPAR)
            .or(AFTER_ALPHANUM_BEFORE_COMMA)
            .or(AFTER_ALPHANUM_BEFORE_LPAR).toString();


    private static final Map<String, TokenType> OPERATORS = Map.of(
            "=", TokenType.ASSIGN,
            "+", TokenType.PLUS,
            "*", TokenType.MULT,
            ">", TokenType.GT,
            "<", TokenType.LT,
            "==", TokenType.EQ
    );

    private static final Map<String, TokenType> PUNCTUATION = Map.of(
            "(", TokenType.LPAR,
            ")", TokenType.RPAR,
            ",", TokenType.COMMA,
            "{", TokenType.LBRACE,
            "}", TokenType.LBRACE
    );

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "if", TokenType.IF,
            "then", TokenType.THEN,
            "else", TokenType.ELSE,
            "while", TokenType.WHILE,
            "do", TokenType.DO,
            "fun", TokenType.FUN,
            "return", TokenType.RETURN
    );

    public List<Token> tokenList = new ArrayList<>();

    public void tokenize(String input) {
        String [] splitInput = input.split(SPLIT_PATTERN);
        mapTokens(splitInput);
    }

    private void mapTokens(String [] splitInput) {
        for (String word : splitInput) {

            //check if it's an operator
            TokenType tokenType = OPERATORS.get(word);

            //check if it's a keyword
            if (isTokenNotFound(tokenType)) {
                tokenType = KEYWORDS.get(word);
            }

            //check if it's punctuation
            if (isTokenNotFound(tokenType)) {
                tokenType = PUNCTUATION.get(word);
            }

            //if still not found, then it's either a value or a variable
            if (isTokenNotFound(tokenType)) {
                tokenType = findDynamicTokenType(word);
            }

            Token token = new Token(tokenType,word);
            this.tokenList.add(token);
        }
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