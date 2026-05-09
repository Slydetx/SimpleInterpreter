package com.tokenizer;

/**
 * Provides character-level access to the source input string. <br>
 * <br>
 * Maintains a current index that advances as characters are consumed.
 */
 class InputConsumer {
    private int index = 0;
    private final char[] chars;

    InputConsumer(String input) {
        chars = input.toCharArray();
    }

    int currentPosition() {
        return index;
    }

    int lastPosition() {
        return chars.length - 1;
    }

    /** Returns the current character and advances the index. */
    char consumeCurrentChar() {
        char c = chars[index];
        advance();
        return c;
    }

    /** Returns the current character without advancing. Returns ' ' at the end of input. */
    char peekCurrentChar() {
        return index < chars.length ? chars[index] : ' ';
    }

    /** Returns the next character without advancing. */
    private char peekNextChar() {
        return chars[index + 1];
    }

    /** Consumes and returns the current two characters as a string. */
    String getTwoChars() {
        String twoChars = Character.toString(chars[index]) + chars[index + 1];
        advance();
        advance();
        return twoChars;
    }

    private void advance() {
        index++;
    }

    /** Returns true if the current character can start a keyword or variable name. */
     boolean currentIsKeywordOrVariable(){
        return Character.isLetter(peekCurrentChar()) || peekCurrentChar() == '_'; //allows names to start with '_'
    }

    /** Returns true if the current character is a digit. */
     boolean currentIsDigit() {
        return Character.isDigit(peekCurrentChar());
    }

    /** Returns true if the current character is a space. */
     boolean currentIsWhiteSpace() {
        return peekCurrentChar() == ' ';
    }

    /**
     * Returns true if the current and next characters form a two-character comparison operator
     * (==, <=, >=, !=).
     */
    public boolean nextTokenIsComparisonOperator() {
        return  (peekCurrentChar() == '='
                || peekCurrentChar() == '<'
                || peekCurrentChar() == '!'
                || peekCurrentChar() == '>') && peekNextChar() == '=';
    }

}
