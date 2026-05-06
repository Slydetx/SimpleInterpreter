package com.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Character;

public class Tokenizer {

    private static final String AFTER_LPAR_BEFORE_DIGIT = "(?<=\\()(?=[\\w(])";
    private static final String AFTER_DIGIT_BEFORE_RPAR = "(?<=[\\w)])(?=\\))";
    private static final String SPLIT_PATTERN = AFTER_LPAR_BEFORE_DIGIT + "|" + AFTER_DIGIT_BEFORE_RPAR + "| ";

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "=", TokenType.EQ,
            "(", TokenType.LPAR,
            ")", TokenType.RPAR,
            "+", TokenType.PLUS,
            "*", TokenType.MULT
    );

    public List<Token> tokenList = new ArrayList<>();

    public void tokenize(String input) {
        String [] splitInput = input.split(SPLIT_PATTERN);
        mapTokens(splitInput);
    }

    private void mapTokens(String [] splitInput) {
        for (String word : splitInput) {
            TokenType tokenType = KEYWORDS.get(word);

            if (!isTokenFound(tokenType)) {
                tokenType = findDynamicTokenType(word);
            }

            Token token = new Token(tokenType,word);
            this.tokenList.add(token);
        }
    }

    private boolean isTokenFound (TokenType tokenType) {
        return tokenType != null;
    }

    private TokenType findDynamicTokenType (String word) {
        boolean wordIsADigit = true;

        for (char character : word.toCharArray()) {
            wordIsADigit &= Character.isDigit(character);
        }

        return wordIsADigit ? TokenType.VAL : TokenType.VAR;
    }
}
