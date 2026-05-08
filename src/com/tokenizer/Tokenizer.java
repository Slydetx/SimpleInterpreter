package com.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Character;

public class Tokenizer {

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
            "}", TokenType.RBRACE
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
        char[] chars = input.toCharArray();
        int charIterator = 0;

        while (charIterator < chars.length) {

            if (charIterator > 0)
                System.out.print(chars[charIterator- 1]);

            if (Character.isLetter(chars[charIterator]) || chars[charIterator] == '_') { //allows names to start with '_'
                StringBuilder word = new StringBuilder();

                while (Character.isLetterOrDigit(chars[charIterator]) || chars[charIterator] == '_') {
                    word.append(chars[charIterator]);
                    charIterator += 1;
                }

                mapToken(word.toString());

            } else if (Character.isDigit(chars[charIterator])) {
                StringBuilder number = new StringBuilder();

                while (Character.isLetterOrDigit(chars[charIterator])) {
                    number.append(chars[charIterator]);
                    charIterator += 1;
                }

                mapToken(number.toString());

            } else if (chars[charIterator] == ' ' ) {

                charIterator += 1;

            } else {

                mapToken(Character.toString(chars[charIterator]));
                charIterator += 1;
            }

        }
    }

    private void mapToken(String rawToken) {

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